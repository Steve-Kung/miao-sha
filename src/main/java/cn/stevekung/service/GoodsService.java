package cn.stevekung.service;

import cn.stevekung.dao.GoodsDao;
import cn.stevekung.dao.MiaoshaUserDao;

import cn.stevekung.domain.Goods;
import cn.stevekung.domain.MiaoshaGoods;
import cn.stevekung.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVO> listGoodsVO(){
        return goodsDao.listGoodsVO();
    }

    public GoodsVO getGoodsVOByGoodsId(Long goodsId) {
        return goodsDao.getGoodsVOByGoodsId(goodsId);
    }

    public void reduceStock(GoodsVO goods) {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        goodsDao.reduceStock(g);
    }
}
