package com.changgou.search.service;

import java.util.Map; /**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.search.service *
 * @since 1.0
 */
public interface SearchService {
    /**
     * 导入数据
     */
    public void importSku();

    /**
     * 根据搜索的条件 执行查询  返回搜索到的结果 map
     * @param searchMap
     * @return
     */
    Map search(Map<String,String> searchMap);
}
