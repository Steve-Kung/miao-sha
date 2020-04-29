package cn.stevekung.controller;

import cn.stevekung.domain.MiaoshaOrder;
import cn.stevekung.domain.MiaoshaUser;

import cn.stevekung.domain.OrderInfo;
import cn.stevekung.rabbitmq.MQSender;
import cn.stevekung.rabbitmq.MiaoshaMessage;
import cn.stevekung.redis.GoodsKey;
import cn.stevekung.redis.MiaoshaKey;
import cn.stevekung.redis.OrderKey;
import cn.stevekung.redis.RedisService;
import cn.stevekung.result.CodeMsg;
import cn.stevekung.result.Result;
import cn.stevekung.service.GoodsService;
import cn.stevekung.service.MiaoshaService;
import cn.stevekung.service.OrderService;
import cn.stevekung.vo.GoodsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    RedisService redisService;
    @Autowired
    MQSender mqSender;

    // 本地超卖标记
    private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();

    private static Logger log = LoggerFactory.getLogger(MiaoshaController.class);


    @RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<GoodsVO> goodsList = goodsService.listGoodsVO();
        for(GoodsVO goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsList);
        return Result.success(true);
    }


//    @RequestMapping("/do_miaosha")
//    public String toList(Model model, MiaoshaUser user,
//                         @RequestParam("goodsId") Long goodsId){
//        if (user == null){
//            return "login";
//        }
//        model.addAttribute("user",user);
//        // 判断库存
//        GoodsVO goods = goodsService.getGoodsVOByGoodsId(goodsId);
//        Integer stock = goods.getStockCount();
//        if (stock <= 0){
//            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
//            return "miaosha_fail";
//        }
//        // 判断是否秒杀到了
//        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
//        if (order != null){
//            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
//            return "miaosha_fail";
//        }
//        // 减库存 下订单 写入秒杀订单
//        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
//        model.addAttribute("orderInfo", orderInfo);
//        model.addAttribute("goods", goods);
//
//        return "order_detail";
//    }

    @RequestMapping(value="/do_miaosha", method= RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user,
                                     @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        /*
        //判断库存
        GoodsVO goods = goodsService.getGoodsVOByGoodsId(goodsId);//10个商品，req1 req2
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        return Result.success(orderInfo);
        */

        // 内存标记， 减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        // 预减库存
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);
        if (stock < 0){
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        // 入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setGoodsId(goodsId);
        mm.setUser(user);
        mqSender.sendMiaoshaMessage(mm);
        return Result.success(0); // 0代表排队中

    }

    /*
        系统初始化 预加载数量到redis中
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVO> goodsList=goodsService.listGoodsVO();
        if (goodsList == null){
            return;
        }
        for (GoodsVO goods : goodsList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false); // 没有超标记
        }
    }

    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result  =miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

}
