package cn.stevekung.dao;

import cn.stevekung.domain.MiaoshaOrder;
import cn.stevekung.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") Long userId,@Param("goodsId") Long goodsId);

    @Insert("insert into order_info(user_id, goods_id, delivery_add_id, goods_name, goods_count, goods_price, order_channel, status, create_date, pay_date) values(" +
            "#{userId}, #{goodsId}, #{deliveryAddId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate}, #{payDate})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = Long.class, before = false, statement = "select last_insert_id()")
    public Long insert(OrderInfo orderInfo);

    @Insert("insert into miaosha_order(user_id, order_id, goods_id) values(#{userId}, #{orderId}, #{goodsId})")
    public int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id = #{orderId}")
    public OrderInfo getOrderById(@Param("orderId")long orderId);
}
