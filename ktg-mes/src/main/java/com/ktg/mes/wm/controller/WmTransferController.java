package com.ktg.mes.wm.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ktg.common.constant.UserConstants;
import com.ktg.mes.wm.service.IWmTransferLineService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
import com.ktg.mes.wm.domain.WmTransfer;
import com.ktg.mes.wm.service.IWmTransferService;
import com.ktg.common.utils.poi.ExcelUtil;
import com.ktg.common.core.page.TableDataInfo;

/**
 * 转移单Controller
 * 
 * @author yinjinlu
 * @date 2022-11-28
 */
@RestController
@RequestMapping("/wm/transfer")
public class WmTransferController extends BaseController
{
    @Autowired
    private IWmTransferService wmTransferService;

    @Autowired
    private IWmTransferLineService wmTransferLineService;

    /**
     * 查询转移单列表
     */
    @PreAuthorize("@ss.hasPermi('wm:transfer:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmTransfer wmTransfer)
    {
        startPage();
        List<WmTransfer> list = wmTransferService.selectWmTransferList(wmTransfer);
        return getDataTable(list);
    }

    /**
     * 导出转移单列表
     */
    @PreAuthorize("@ss.hasPermi('wm:transfer:export')")
    @Log(title = "转移单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WmTransfer wmTransfer)
    {
        List<WmTransfer> list = wmTransferService.selectWmTransferList(wmTransfer);
        ExcelUtil<WmTransfer> util = new ExcelUtil<WmTransfer>(WmTransfer.class);
        util.exportExcel(response, list, "转移单数据");
    }

    /**
     * 获取转移单详细信息
     */
    @PreAuthorize("@ss.hasPermi('wm:transfer:query')")
    @GetMapping(value = "/{transferId}")
    public AjaxResult getInfo(@PathVariable("transferId") Long transferId)
    {
        return AjaxResult.success(wmTransferService.selectWmTransferByTransferId(transferId));
    }

    /**
     * 新增转移单
     */
    @PreAuthorize("@ss.hasPermi('wm:transfer:add')")
    @Log(title = "转移单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WmTransfer wmTransfer)
    {
        if(UserConstants.NOT_UNIQUE.equals(wmTransferService.checkUnique(wmTransfer))){
            return AjaxResult.error("转移单编号已存在");
        }
        return toAjax(wmTransferService.insertWmTransfer(wmTransfer));
    }

    /**
     * 修改转移单
     */
    @PreAuthorize("@ss.hasPermi('wm:transfer:edit')")
    @Log(title = "转移单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WmTransfer wmTransfer)
    {
        if(UserConstants.NOT_UNIQUE.equals(wmTransferService.checkUnique(wmTransfer))){
            return AjaxResult.error("转移单编号已存在");
        }
        return toAjax(wmTransferService.updateWmTransfer(wmTransfer));
    }

    /**
     * 删除转移单
     */
    @PreAuthorize("@ss.hasPermi('wm:transfer:remove')")
    @Log(title = "转移单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{transferIds}")
    @Transactional
    public AjaxResult remove(@PathVariable Long[] transferIds)
    {
        for (Long transferId:transferIds
             ) {
            wmTransferLineService.deleteByTransferId(transferId);
        }
        return toAjax(wmTransferService.deleteWmTransferByTransferIds(transferIds));
    }
}