package com.lzlj.lzlj.goods.service;

import com.lzlj.lzlj.goods.vo.GoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 商品服务 - 本地服务调用示例
 */
@Slf4j
@Service
public class GoodsService {

    private final Map<Long, GoodsVO> goodsMap = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1000);

    @PostConstruct
    public void init() {
        // 初始化一些假数据
        initMockData();
    }

    private void initMockData() {
        for (int i = 1; i <= 5; i++) {
            GoodsVO goods = new GoodsVO();
            goods.setId((long) i);
            goods.setName("商品-" + i);
            goods.setCategory(i % 2 == 0 ? "食品" : "饮料");
            goods.setPrice(BigDecimal.valueOf(10.0 + i * 5));
            goods.setStock(100 + i * 10);
            goods.setUnit("件");
            goods.setDescription("这是商品 " + i + " 的描述");
            goods.setImageUrl("https://example.com/goods/" + i + ".jpg");
            goods.setStatus(1);
            goods.setCreatorId(1L);
            goods.setCreatorName("管理员");
            goods.setCreateTime(LocalDateTime.now().minusDays(i));
            goods.setUpdateTime(LocalDateTime.now());
            goodsMap.put(goods.getId(), goods);
        }
    }

    /**
     * 本地服务调用示例 - 获取商品列表
     */
    public List<GoodsVO> list() {
        log.info("本地服务调用: 获取商品列表");
        return new ArrayList<>(goodsMap.values());
    }

    /**
     * 本地服务调用示例 - 根据ID获取商品
     */
    public GoodsVO getById(Long id) {
        log.info("本地服务调用: 获取商品详情, id={}", id);
        return goodsMap.get(id);
    }

    /**
     * 本地服务调用示例 - 分页查询
     */
    public List<GoodsVO> page(int pageNum, int pageSize) {
        log.info("本地服务调用: 分页查询商品, pageNum={}, pageSize={}", pageNum, pageSize);
        List<GoodsVO> allGoods = new ArrayList<>(goodsMap.values());
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allGoods.size());
        if (start >= allGoods.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(allGoods.subList(start, end));
    }

    /**
     * 本地服务调用示例 - 创建商品
     */
    public Long create(GoodsVO goods) {
        log.info("本地服务调用: 创建商品, name={}", goods.getName());
        Long id = idGenerator.incrementAndGet();
        goods.setId(id);
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        goodsMap.put(id, goods);
        return id;
    }
}
