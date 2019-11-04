package com.changgou.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.changgou.item.feign.PageFeign;
import com.xpand.starter.canal.annotation.*;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.listener *
 * @since 1.0
 */
@CanalEventListener // 注解用于监听canal-server端的数据的变化
public class MyEventListener {

    @InsertListenPoint//当发送insert的操作的时候触发以下的方法的调用
    public void onEvent(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //do something...

    }

    /*@UpdateListenPoint //当发送update的操作的时候触发以下的方法的调用
    // rowData  就是当数据被修改的时候,变化的数据
    public void onEvent1(CanalEntry.RowData rowData) {
        //do something... 获取变化后的数据
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            System.out.println(column.getName() + ":" + column.getValue());
        }
        System.out.println();
    }*/

    @DeleteListenPoint  //当发送delete的操作的时候触发以下的方法的调用
    public void onEvent3(CanalEntry.EventType eventType) {
        //do something...
    }

    @Autowired
    private ContentFeign contentFeign;

    @Autowired
    private RedisTemplate redisTemplate;


    //自定义事件 处理
    //destination 指定目的地 exmaple
    // schema 指定数据库的库名
    // table 指定监听的表
    // eventType 指定监听的操作的数据类型(create index update insert delete)
    @ListenPoint(destination = "example", schema = "changgou_content", table = {"tb_content"}, eventType = {CanalEntry.EventType.DELETE, CanalEntry.EventType.INSERT, CanalEntry.EventType.UPDATE})
    public void onEvent4(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //1.先获取到被修改的商品分类的ID
        String categoryId = getColumnValue(eventType, rowData);
        //2.调用广告微服务的feign的方法 (根据广告分类的ID 获取该广告分类下的所有的广告的列表数据)
        Result<List<Content>> contentListResult = contentFeign.findByCategory(Long.valueOf(categoryId));
        //3.从新设置回redis中
        List<Content> data = contentListResult.getData();
        redisTemplate.boundValueOps("content_" + categoryId).set(JSON.toJSONString(data));
    }

    /**
     * 根据数据来获取该category_id的值,需要进行判断
     *
     * @param eventType
     * @param rowData
     * @return
     */
    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String category_id = "";
        //1.判断如果是delete  要删除
        if (eventType == CanalEntry.EventType.DELETE) {
            //2.获取删除之前的数据 获取里面的category_id的值
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                if ("category_id".equalsIgnoreCase(column.getName())) {
                    category_id = column.getValue();
                    break;
                }
            }
        } else {
            //3.判断如果是update /insert 要新增 要修改 获取之后的值 获取里面的category_id的值
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            for (CanalEntry.Column column : afterColumnsList) {
                if ("category_id".equalsIgnoreCase(column.getName())) {
                    category_id = column.getValue();
                    break;
                }
            }
        }
        //4.返回
        return category_id;
    }



    @Autowired
    private PageFeign pageFeign;


    /**
     * 监听SPU的表的数据的变化,,获取到变化的数据(SPU的ID)
     * 调用生成静态页的feign的方法 生成静态页即可
     * @param eventType
     * @param rowData
     */

    @ListenPoint(destination = "example",
            schema = "changgou_goods",
            table = {"tb_spu"},
            eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.INSERT, CanalEntry.EventType.DELETE})
    public void onEventCustomSpu(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        //判断操作类型
        if (eventType == CanalEntry.EventType.DELETE) {
            String spuId = "";
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                if (column.getName().equals("id")) {
                    spuId = column.getValue();//spuid
                    break;
                }
            }
            //todo 删除静态页

        }else{
            //新增 或者 更新
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            String spuId = "";
            for (CanalEntry.Column column : afterColumnsList) {
                if (column.getName().equals("id")) {
                    spuId = column.getValue();
                    break;
                }
            }
            //更新 生成静态页
            pageFeign.createHtml(Long.valueOf(spuId));
        }
    }


}
