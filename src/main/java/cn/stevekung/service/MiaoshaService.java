package cn.stevekung.service;

import cn.stevekung.dao.GoodsDao;
import cn.stevekung.domain.Goods;
import cn.stevekung.domain.MiaoshaOrder;
import cn.stevekung.domain.MiaoshaUser;
import cn.stevekung.domain.OrderInfo;
import cn.stevekung.redis.MiaoshaKey;
import cn.stevekung.redis.RedisService;
import cn.stevekung.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    // 减库存 下订单 写入秒杀订单
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVO goods) {
        boolean success = goodsService.reduceStock(goods);

        if (success){
            // order_info miaosha_order
            return orderService.createOrder(user, goods);
        } else {
            // 设置秒杀商品卖完了 标记
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if(order != null) {//秒杀成功
            return order.getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
    }

    public void reset(List<GoodsVO> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }
}
