package com.ktg.mes.wm.service;

import com.ktg.mes.wm.domain.WmItemRecptLine;
import com.ktg.mes.wm.domain.tx.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IStorageCoreService {

    /**
     * 处理物料入库单
     * @param lines
     */
    public void processItemRecpt(List<ItemRecptTxBean> lines);

    /**
     * 处理供应商退货单
     * @param lines
     */
    public void processRtVendor(List<RtVendorTxBean> lines);

    /**
     * 处理生产领料
     * @param lines
     */
    public void processIssue(List<IssueTxBean> lines);

    /**
     * 处理生产退料
     * @param lines
     */
    public void processRtIssue(List<RtIssueTxBean> lines);

    /**
     * 处理产品入库
     * @param lines
     */
    public void processProductRecpt(List<ProductRecptTxBean> lines);

    /**
     * 处理产品销售出库
     * @param lines
     */
    public void processProductSalse(List<ProductSalseTxBean> lines);

}
