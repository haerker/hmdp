package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Result getTypeList() {
        ArrayList<ShopType> shopTypes = new ArrayList<>();
        List<String> s = stringRedisTemplate.opsForList().range("cache:shop-type", 0, -1);
        if (s != null && !s.isEmpty()) {
            for (String string : s) {
                ShopType bean = JSONUtil.toBean(string, ShopType.class);
                shopTypes.add(bean);
            }
            return Result.ok(shopTypes);
        }
        List<ShopType> list = query().orderByAsc("sort").list();
        for (ShopType shopType : list) {
            stringRedisTemplate.opsForList().rightPush("cache:shop-type", JSONUtil.toJsonStr(shopType));
        }
        return Result.ok(list);
    }
}
