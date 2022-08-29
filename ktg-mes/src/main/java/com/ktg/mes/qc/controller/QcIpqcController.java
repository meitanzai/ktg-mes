package com.ktg.mes.qc.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ktg.common.constant.UserConstants;
import com.ktg.common.utils.StringUtils;
import com.ktg.mes.pro.domain.ProWorkorder;
import com.ktg.mes.pro.service.IProWorkorderService;
import com.ktg.mes.qc.domain.QcTemplate;
import com.ktg.mes.qc.service.IQcTemplateService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ktg.common.annotation.Log;
import com.ktg.common.core.controller.BaseController;
import com.ktg.common.core.domain.AjaxResult;
import com.ktg.common.enums.BusinessType;
import com.ktg.mes.qc.domain.QcIpqc;
import com.ktg.mes.qc.service.IQcIpqcService;
import com.ktg.common.utils.poi.ExcelUtil;
import com.ktg.common.core.page.TableDataInfo;

/**
 * 过程检验单Controller
 * 
 * @author yinjinlu
 * @date 2022-08-29
 */
@RestController
@RequestMapping("/mes/qc/ipqc")
public class QcIpqcController extends BaseController
{
    @Autowired
    private IQcIpqcService qcIpqcService;

    @Autowired
    private IProWorkorderService proWorkorderService;

    @Autowired
    private IQcTemplateService qcTemplateService;

    /**
     * 查询过程检验单列表
     */
    @PreAuthorize("@ss.hasPermi('mes:qc:ipqc:list')")
    @GetMapping("/list")
    public TableDataInfo list(QcIpqc qcIpqc)
    {
        startPage();
        List<QcIpqc> list = qcIpqcService.selectQcIpqcList(qcIpqc);
        return getDataTable(list);
    }

    /**
     * 导出过程检验单列表
     */
    @PreAuthorize("@ss.hasPermi('mes:qc:ipqc:export')")
    @Log(title = "过程检验单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, QcIpqc qcIpqc)
    {
        List<QcIpqc> list = qcIpqcService.selectQcIpqcList(qcIpqc);
        ExcelUtil<QcIpqc> util = new ExcelUtil<QcIpqc>(QcIpqc.class);
        util.exportExcel(response, list, "过程检验单数据");
    }

    /**
     * 获取过程检验单详细信息
     */
    @PreAuthorize("@ss.hasPermi('mes:qc:ipqc:query')")
    @GetMapping(value = "/{ipqcId}")
    public AjaxResult getInfo(@PathVariable("ipqcId") Long ipqcId)
    {
        return AjaxResult.success(qcIpqcService.selectQcIpqcByIpqcId(ipqcId));
    }

    /**
     * 新增过程检验单
     */
    @PreAuthorize("@ss.hasPermi('mes:qc:ipqc:add')")
    @Log(title = "过程检验单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QcIpqc qcIpqc)
    {
        if(UserConstants.NOT_UNIQUE.equals(qcIpqcService.checkIpqcCodeUnique(qcIpqc))){
            return AjaxResult.error("检测单编码已存在！");
        }

        //根据工单获取产品信息
        ProWorkorder workorder = proWorkorderService.selectProWorkorderByWorkorderId(qcIpqc.getWorkorderId());
        qcIpqc.setWorkorderId(workorder.getWorkorderId());
        qcIpqc.setWorkorderCode(workorder.getWorkorderCode());
        qcIpqc.setWorkorderName(workorder.getWorkorderName());
        qcIpqc.setItemId(workorder.getProductId());
        qcIpqc.setItemCode(workorder.getProductCode());
        qcIpqc.setItemName(workorder.getProductName());
        qcIpqc.setSpecification(workorder.getProductSpc());
        qcIpqc.setUnitOfMeasure(workorder.getUnitOfMeasure());

        //根据产品和检测类型获取检测模板
        QcTemplate param = new QcTemplate();
        param.setQcTypes(qcIpqc.getIpqcType());
        param.setItemId(workorder.getProductId());
        QcTemplate template = qcTemplateService.selectQcTemplateByProductAndQcType(param);
        if(StringUtils.isNotNull(template)){
            qcIpqc.setTemplateId(template.getTemplateId());
        }else{
            return AjaxResult.error("当前工单生产的产品未配置此类型的检验模板！");
        }

        return toAjax(qcIpqcService.insertQcIpqc(qcIpqc));
    }

    /**
     * 修改过程检验单
     */
    @PreAuthorize("@ss.hasPermi('mes:qc:ipqc:edit')")
    @Log(title = "过程检验单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QcIpqc qcIpqc)
    {
        if(UserConstants.NOT_UNIQUE.equals(qcIpqcService.checkIpqcCodeUnique(qcIpqc))){
            return AjaxResult.error("检测单编码已存在！");
        }
        return toAjax(qcIpqcService.updateQcIpqc(qcIpqc));
    }

    /**
     * 删除过程检验单
     */
    @PreAuthorize("@ss.hasPermi('mes:qc:ipqc:remove')")
    @Log(title = "过程检验单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ipqcIds}")
    public AjaxResult remove(@PathVariable Long[] ipqcIds)
    {
        return toAjax(qcIpqcService.deleteQcIpqcByIpqcIds(ipqcIds));
    }
}