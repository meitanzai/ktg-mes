package com.ktg.mes.qc.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.ktg.common.constant.UserConstants;
import com.ktg.common.utils.StringUtils;
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
import com.ktg.mes.qc.domain.QcTemplate;
import com.ktg.mes.qc.service.IQcTemplateService;
import com.ktg.common.utils.poi.ExcelUtil;
import com.ktg.common.core.page.TableDataInfo;

/**
 * 检测模板Controller
 * 
 * @author yinjinlu
 * @date 2022-05-17
 */
@RestController
@RequestMapping("/mes/qc/qctemplate")
public class QcTemplateController extends BaseController
{
    @Autowired
    private IQcTemplateService qcTemplateService;

    /**
     * 查询检测模板列表
     */
    @PreAuthorize("@ss.hasPermi('mes:qc:qctemplate:list')")
    @GetMapping("/list")
    public TableDataInfo list(QcTemplate qcTemplate)
    {
        startPage();
        List<QcTemplate> list = qcTemplateService.selectQcTemplateList(qcTemplate);
        if(CollUtil.isNotEmpty(list)){
            int i=0;
            for (QcTemplate template: list
                    ) {
                template.setQcTypesParam(template.getQcTypes().split(","));
                list.set(i,template);
            }
            i++;
        }
        return getDataTable(list);
    }

    /**
     * 导出检测模板列表
     */
    @PreAuthorize("@ss.hasPermi('mes:qc:qctemplate:export')")
    @Log(title = "检测模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, QcTemplate qcTemplate)
    {
        List<QcTemplate> list = qcTemplateService.selectQcTemplateList(qcTemplate);
        ExcelUtil<QcTemplate> util = new ExcelUtil<QcTemplate>(QcTemplate.class);
        util.exportExcel(response, list, "检测模板数据");
    }

    /**
     * 获取检测模板详细信息
     */
    @PreAuthorize("@ss.hasPermi('mes:qc:qctemplate:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId)
    {
        return AjaxResult.success(qcTemplateService.selectQcTemplateByTemplateId(templateId));
    }

    /**
     * 新增检测模板
     */
    @PreAuthorize("@ss.hasPermi('mes:qc:qctemplate:add')")
    @Log(title = "检测模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QcTemplate qcTemplate)
    {
        if(UserConstants.NOT_UNIQUE.equals(qcTemplateService.checkTemplateCodeUnique(qcTemplate))){
            return AjaxResult.error("检测模板编号已存在！");
        }

        if(ArrayUtil.isNotEmpty(qcTemplate.getQcTypesParam())){
            String[] types = qcTemplate.getQcTypesParam();
            for (String type:types
                 ) {
                if(StringUtils.isNotNull(qcTemplate.getQcTypes())){
                    qcTemplate.setQcTypes(qcTemplate.getQcTypes()+','+type);
                }else{
                    qcTemplate.setQcTypes(type);
                }
            }
        }

        return toAjax(qcTemplateService.insertQcTemplate(qcTemplate));
    }

    /**
     * 修改检测模板
     */
    @PreAuthorize("@ss.hasPermi('mes:qc:qctemplate:edit')")
    @Log(title = "检测模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QcTemplate qcTemplate)
    {
        if(UserConstants.NOT_UNIQUE.equals(qcTemplateService.checkTemplateCodeUnique(qcTemplate))){
            return AjaxResult.error("检测模板编号已存在！");
        }
        if(ArrayUtil.isNotEmpty(qcTemplate.getQcTypesParam())){
            String[] types = qcTemplate.getQcTypesParam();
            for (String type:types
                    ) {
                if(StringUtils.isNotNull(qcTemplate.getQcTypes())){
                    qcTemplate.setQcTypes(qcTemplate.getQcTypes()+','+type);
                }else{
                    qcTemplate.setQcTypes(type);
                }
            }
        }
        return toAjax(qcTemplateService.updateQcTemplate(qcTemplate));
    }

    /**
     * 删除检测模板
     */
    @PreAuthorize("@ss.hasPermi('mes:qc:qctemplate:remove')")
    @Log(title = "检测模板", businessType = BusinessType.DELETE)
	@DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds)
    {
        return toAjax(qcTemplateService.deleteQcTemplateByTemplateIds(templateIds));
    }
}
