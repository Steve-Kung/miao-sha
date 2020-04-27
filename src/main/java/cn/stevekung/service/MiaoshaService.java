package cn.stevekung.service;

import cn.stevekung.dao.GoodsDao;
import cn.stevekung.domain.Goods;
import cn.stevekung.domain.MiaoshaUser;
import cn.stevekung.domain.OrderInfo;
import cn.stevekung.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    // 减库存 下订单 写入秒杀订单
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVO goods) {
        goodsService.reduceStock(goods);

        // order_info miaosha_order
        return orderService.createOrder(user, goods);

    }
}
