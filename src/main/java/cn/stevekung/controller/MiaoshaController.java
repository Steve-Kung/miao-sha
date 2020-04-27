package cn.stevekung.controller;

import cn.stevekung.domain.MiaoshaOrder;
import cn.stevekung.domain.MiaoshaUser;

import cn.stevekung.domain.OrderInfo;
import cn.stevekung.result.CodeMsg;
import cn.stevekung.service.GoodsService;
import cn.stevekung.service.MiaoshaService;
import cn.stevekung.service.OrderService;
import cn.stevekung.vo.GoodsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;

    private static Logger log = LoggerFactory.getLogger(MiaoshaController.class);


    @RequestMapping("/do_miaosha")
    public String toList(Model model, MiaoshaUser user,
                         @RequestParam("goodsId") Long goodsId){
        if (user == null){
            return "login";
        }
        model.addAttribute("user",user);
        // 判断库存
        GoodsVO goods = goodsService.getGoodsVOByGoodsId(goodsId);
        Integer stock = goods.getStockCount();
        if (stock <= 0){
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        // 判断是否秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null){
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        // 减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);

        return "order_detail";
    }

}
