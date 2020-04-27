package cn.stevekung.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MiaoshaGoods {
	private Long id;
	private Long goodsId;
	private Double miaosha_price;
	private Integer stockCount;
	private Date startDate;
	private Date endDate;
}
