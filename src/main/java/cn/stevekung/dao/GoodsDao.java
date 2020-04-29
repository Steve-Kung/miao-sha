package cn.stevekung.dao;

import cn.stevekung.domain.Goods;
import cn.stevekung.domain.MiaoshaGoods;
import cn.stevekung.domain.MiaoshaUser;
import cn.stevekung.vo.GoodsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {
    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVO> listGoodsVO();

    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
    public GoodsVO getGoodsVOByGoodsId(@Param("goodsId") Long goodsId);

    @Update("update miaosha_goods set stock_count = stock_count -1 where goods_id = #{goodsId} and stock_count > 0")
    public int reduceStock(MiaoshaGoods g);

    @Update("update miaosha_goods set stock_count = #{stockCount} where goods_id = #{goodsId}")
    public int resetStock(MiaoshaGoods g);

}
