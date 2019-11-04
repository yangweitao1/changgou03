package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:admin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;


    /**
     * Spu条件+分页查询
     *
     * @param spu  查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     *
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu) {
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     *
     * @param spu
     * @return
     */
    public Example createExample(Spu spu) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (spu != null) {
            // 主键
            if (!StringUtils.isEmpty(spu.getId())) {
                criteria.andEqualTo("id", spu.getId());
            }
            // 货号
            if (!StringUtils.isEmpty(spu.getSn())) {
                criteria.andEqualTo("sn", spu.getSn());
            }
            // SPU名
            if (!StringUtils.isEmpty(spu.getName())) {
                criteria.andLike("name", "%" + spu.getName() + "%");
            }
            // 副标题
            if (!StringUtils.isEmpty(spu.getCaption())) {
                criteria.andEqualTo("caption", spu.getCaption());
            }
            // 品牌ID
            if (!StringUtils.isEmpty(spu.getBrandId())) {
                criteria.andEqualTo("brandId", spu.getBrandId());
            }
            // 一级分类
            if (!StringUtils.isEmpty(spu.getCategory1Id())) {
                criteria.andEqualTo("category1Id", spu.getCategory1Id());
            }
            // 二级分类
            if (!StringUtils.isEmpty(spu.getCategory2Id())) {
                criteria.andEqualTo("category2Id", spu.getCategory2Id());
            }
            // 三级分类
            if (!StringUtils.isEmpty(spu.getCategory3Id())) {
                criteria.andEqualTo("category3Id", spu.getCategory3Id());
            }
            // 模板ID
            if (!StringUtils.isEmpty(spu.getTemplateId())) {
                criteria.andEqualTo("templateId", spu.getTemplateId());
            }
            // 运费模板id
            if (!StringUtils.isEmpty(spu.getFreightId())) {
                criteria.andEqualTo("freightId", spu.getFreightId());
            }
            // 图片
            if (!StringUtils.isEmpty(spu.getImage())) {
                criteria.andEqualTo("image", spu.getImage());
            }
            // 图片列表
            if (!StringUtils.isEmpty(spu.getImages())) {
                criteria.andEqualTo("images", spu.getImages());
            }
            // 售后服务
            if (!StringUtils.isEmpty(spu.getSaleService())) {
                criteria.andEqualTo("saleService", spu.getSaleService());
            }
            // 介绍
            if (!StringUtils.isEmpty(spu.getIntroduction())) {
                criteria.andEqualTo("introduction", spu.getIntroduction());
            }
            // 规格列表
            if (!StringUtils.isEmpty(spu.getSpecItems())) {
                criteria.andEqualTo("specItems", spu.getSpecItems());
            }
            // 参数列表
            if (!StringUtils.isEmpty(spu.getParaItems())) {
                criteria.andEqualTo("paraItems", spu.getParaItems());
            }
            // 销量
            if (!StringUtils.isEmpty(spu.getSaleNum())) {
                criteria.andEqualTo("saleNum", spu.getSaleNum());
            }
            // 评论数
            if (!StringUtils.isEmpty(spu.getCommentNum())) {
                criteria.andEqualTo("commentNum", spu.getCommentNum());
            }
            // 是否上架
            if (!StringUtils.isEmpty(spu.getIsMarketable())) {
                criteria.andEqualTo("isMarketable", spu.getIsMarketable());
            }
            // 是否启用规格
            if (!StringUtils.isEmpty(spu.getIsEnableSpec())) {
                criteria.andEqualTo("isEnableSpec", spu.getIsEnableSpec());
            }
            // 是否删除
            if (!StringUtils.isEmpty(spu.getIsDelete())) {
                criteria.andEqualTo("isDelete", spu.getIsDelete());
            }
            // 审核状态
            if (!StringUtils.isEmpty(spu.getStatus())) {
                criteria.andEqualTo("status", spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     *
     * @param spu
     */
    @Override
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     *
     * @param spu
     */
    @Override
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }


    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据页码传递的SPU的ID 是否有值来判断到底是新增(没有) 还是 修改(有)
     *
     * @param goods
     */

    @Override
    public void save(Goods goods) {
        //1.先获取到SPU的数据
        Spu spu = goods.getSpu();

        //1.1 先生成主键(唯一),再设置主键  雪花算法来生成全局唯一的ID
        long spuid = idWorker.nextId();

        if (spu.getId() == null) {
            //新增
            spu.setId(spuid);
            //3.插入数据到SPU表
            spuMapper.insertSelective(spu);

        } else {
            //修改
            spuMapper.updateByPrimaryKeySelective(spu);

            //修改SKU的数据:采取:1.先删除原本的SKU的列表  2.再新增 页面传递过来的额SKU的列表
            //delete from tb_sku where spu_id=?
            Sku condition = new Sku();
            condition.setSpuId(spu.getId());
            skuMapper.delete(condition);
        }


        //2.获取到SKU的列表数据
        List<Sku> skuList = goods.getSkuList();
        //4.循环遍历SKU的列表 插入数据到SKU表中

        for (Sku sku : skuList) {

            //4.1 生成SKU的主键
            long skuid = idWorker.nextId();
            sku.setId(skuid);
            //4.2 设置sku的名称   要求:SPU的名称+" "+ 规格的选项的值 拼接起来
            // 获取规格的数据,再将其数据 解析出 规格的具体选项值 拼接即可
            String name = spu.getName();

            String spec = sku.getSpec();//{"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"165"}

            Map<String, String> map = JSON.parseObject(spec, Map.class);

            for (Map.Entry<String, String> entry : map.entrySet()) {
                String value = entry.getValue();//"立体声"
                name += " " + value;
            }
            sku.setName(name);

            //4.3 设置创建时间
            sku.setCreateTime(new Date());

            sku.setUpdateTime(sku.getCreateTime());
            //4.4. 设置SPU的id (外键)
            sku.setSpuId(spu.getId());

            //4.5 设置三级分类的ID 和名称 (先从SPU获取到商品分类的ID,根据商品的分类的ID 获取商品分类的对象)

            Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
            sku.setCategoryId(category.getId());
            sku.setCategoryName(category.getName());

            //4.6 设置品牌的名称 (先从SPU获取品牌的ID 再获取品牌对象 )
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            sku.setBrandName(brand.getName());

            skuMapper.insertSelective(sku);
        }
    }

    @Override
    public Goods findGoodsById(Long id) {
        //1.根据spu的ID 查询SPU的数据
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //2.根据SPU的ID 查询SKU的列表数据
        // select * from tb_sku where spu_id=?
        Sku condition = new Sku();
        condition.setSpuId(id);
        List<Sku> skuList = skuMapper.select(condition);
        //3.组合对象 返回
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }

    @Override
    public void audit(Long id) {
        //1.根据商品的ID 获取SPU的对象数据
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu.getIsDelete().equalsIgnoreCase("1")) {
            //被删除了
            throw new RuntimeException("不能审核删除的商品");
        }
        //2.更新审核的状态值
        spu.setStatus("1");//变成已审核
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void put(Long id) {
        //1.先根据id获取SPU的数据
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //2.判断是否符合条件
        if (spu.getIsDelete().equalsIgnoreCase("1")) {
            throw new RuntimeException("不能商家删除的商品");
        }

        if (spu.getStatus().equalsIgnoreCase("0")) {
            //未审核
            throw new RuntimeException("不能上架未审核的商品");
        }
        //3.更新状态
        spu.setIsMarketable("1");//上架

        spuMapper.updateByPrimaryKeySelective(spu);

    }

    @Override
    public void pull(Long id) {
        //1.查询商品的数据
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //2.判断条件
        if (spu.getIsDelete().equalsIgnoreCase("1")) {
            throw new RuntimeException("不能商家删除的商品");
        }
        //3.更新状态
        spu.setIsMarketable("0");//下架
        spuMapper.updateByPrimaryKeySelective(spu);
    }
}
