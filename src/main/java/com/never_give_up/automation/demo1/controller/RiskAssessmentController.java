package com.never_give_up.automation.demo1.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.never_give_up.automation.demo1.dto.request.*;
import com.never_give_up.automation.demo1.service.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/risk-assessment")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    private String getUserId(HttpServletRequest request) {
        // 从请求头中获取用户ID
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            userId = "default_user";
        }
        return userId;
    }

    // ==================== 风险评估实例相关 ====================

    @PostMapping("/workflows")
    public ResponseEntity<String> createWorkflow(@RequestBody CreateWorkflowRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.createWorkflow(request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/by-external-task/{externalTaskId}")
    public ResponseEntity<String> getWorkflowByExternalTask(@PathVariable String externalTaskId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getWorkflowByExternalTask(externalTaskId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}")
    public ResponseEntity<String> getWorkflowDetail(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getWorkflowDetail(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/workflows/{workflowInstanceId}")
    public ResponseEntity<String> updateWorkflow(@PathVariable String workflowInstanceId, @RequestBody UpdateWorkflowRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.updateWorkflow(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 资料相关 ====================

    @PostMapping("/workflows/{workflowInstanceId}/materials")
    public ResponseEntity<String> uploadMaterial(
            @PathVariable String workflowInstanceId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "material_type", required = false) String materialType,
            @RequestParam(value = "uploaded_by", required = false) String uploadedBy,
            @RequestParam(value = "remark", required = false) String remark,
            HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        UploadMaterialRequest request = new UploadMaterialRequest();
        request.setFile(file);
        request.setMaterialType(materialType);
        request.setUploadedBy(uploadedBy);
        request.setRemark(remark);
        String result = riskAssessmentService.uploadMaterial(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/materials")
    public ResponseEntity<String> getMaterials(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getMaterials(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/workflows/{workflowInstanceId}/materials/{materialId}")
    public ResponseEntity<String> updateMaterial(
            @PathVariable String workflowInstanceId,
            @PathVariable String materialId,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.updateMaterial(workflowInstanceId, materialId, request, userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/workflows/{workflowInstanceId}/materials/{materialId}")
    public ResponseEntity<String> deleteMaterial(@PathVariable String workflowInstanceId, @PathVariable String materialId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.deleteMaterial(workflowInstanceId, materialId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/materials/{materialId}/parse")
    public ResponseEntity<String> parseMaterial(
            @PathVariable String workflowInstanceId,
            @PathVariable String materialId,
            @RequestBody(required = false) ParseMaterialRequest request,
            HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        if (request == null) {
            request = new ParseMaterialRequest();
        }
        String result = riskAssessmentService.parseMaterial(workflowInstanceId, materialId, request, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/materials/{materialId}/dify-parse")
    public ResponseEntity<String> difyParseMaterial(
            @PathVariable String workflowInstanceId,
            @PathVariable String materialId,
            @RequestBody DifyParseRequest request,
            HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.difyParseMaterial(workflowInstanceId, materialId, request, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/materials/{materialId}/parsed-fields")
    public ResponseEntity<String> setParsedFields(
            @PathVariable String workflowInstanceId,
            @PathVariable String materialId,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.setParsedFields(workflowInstanceId, materialId, request, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 资料解析阶段 ====================

    @PostMapping("/workflows/{workflowInstanceId}/stage/material-parse/run")
    public ResponseEntity<String> runMaterialParseStage(@PathVariable String workflowInstanceId, @RequestBody StageRunRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.runMaterialParseStage(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/stage/material-parse")
    public ResponseEntity<String> getMaterialParseStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getMaterialParseStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/stage/material-parse/confirm")
    public ResponseEntity<String> confirmMaterialParseStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.confirmMaterialParseStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 资料完整性检查 ====================

    @PostMapping("/workflows/{workflowInstanceId}/stage/completeness-check/run")
    public ResponseEntity<String> runCompletenessCheckStage(@PathVariable String workflowInstanceId, @RequestBody StageRunRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.runCompletenessCheckStage(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/stage/completeness-check")
    public ResponseEntity<String> getCompletenessCheckStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getCompletenessCheckStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/stage/completeness-check/confirm")
    public ResponseEntity<String> confirmCompletenessCheckStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.confirmCompletenessCheckStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 评估范围确认 ====================

    @GetMapping("/workflows/{workflowInstanceId}/stage/scope-confirm/options")
    public ResponseEntity<String> getScopeOptions(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getScopeOptions(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/stage/scope-confirm/select")
    public ResponseEntity<String> selectScope(@PathVariable String workflowInstanceId, @RequestBody ScopeConfirmSelectRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.selectScope(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/stage/scope-confirm/run")
    public ResponseEntity<String> runScopeConfirmStage(@PathVariable String workflowInstanceId, @RequestBody StageRunRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.runScopeConfirmStage(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/stage/scope-confirm")
    public ResponseEntity<String> getScopeConfirmStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getScopeConfirmStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/stage/scope-confirm/confirm")
    public ResponseEntity<String> confirmScopeConfirmStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.confirmScopeConfirmStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 风险单元划分 ====================

    @PostMapping("/workflows/{workflowInstanceId}/stage/risk-unit/run")
    public ResponseEntity<String> runRiskUnitStage(@PathVariable String workflowInstanceId, @RequestBody StageRunRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.runRiskUnitStage(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/stage/risk-unit")
    public ResponseEntity<String> getRiskUnitStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getRiskUnitStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/stage/risk-unit/confirm")
    public ResponseEntity<String> confirmRiskUnitStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.confirmRiskUnitStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 风险点识别 ====================

    @PostMapping("/workflows/{workflowInstanceId}/stage/risk-point/run")
    public ResponseEntity<String> runRiskPointStage(@PathVariable String workflowInstanceId, @RequestBody StageRunRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.runRiskPointStage(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/stage/risk-point")
    public ResponseEntity<String> getRiskPointStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getRiskPointStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/stage/risk-point/confirm")
    public ResponseEntity<String> confirmRiskPointStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.confirmRiskPointStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 危险源辨识 ====================

    @PostMapping("/workflows/{workflowInstanceId}/stage/hazard-identification/run")
    public ResponseEntity<String> runHazardIdentificationStage(@PathVariable String workflowInstanceId, @RequestBody StageRunRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.runHazardIdentificationStage(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/stage/hazard-identification")
    public ResponseEntity<String> getHazardIdentificationStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getHazardIdentificationStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/stage/hazard-identification/confirm")
    public ResponseEntity<String> confirmHazardIdentificationStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.confirmHazardIdentificationStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 风险评价 ====================

    @PostMapping("/workflows/{workflowInstanceId}/stage/risk-evaluation/run")
    public ResponseEntity<String> runRiskEvaluationStage(@PathVariable String workflowInstanceId, @RequestBody StageRunRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.runRiskEvaluationStage(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/stage/risk-evaluation")
    public ResponseEntity<String> getRiskEvaluationStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getRiskEvaluationStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/stage/risk-evaluation/confirm")
    public ResponseEntity<String> confirmRiskEvaluationStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.confirmRiskEvaluationStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 管控措施 ====================

    @PostMapping("/workflows/{workflowInstanceId}/stage/control-measure/run")
    public ResponseEntity<String> runControlMeasureStage(@PathVariable String workflowInstanceId, @RequestBody StageRunRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.runControlMeasureStage(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/stage/control-measure")
    public ResponseEntity<String> getControlMeasureStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getControlMeasureStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/stage/control-measure/confirm")
    public ResponseEntity<String> confirmControlMeasureStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.confirmControlMeasureStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 任务与检查项 ====================

    @PostMapping("/workflows/{workflowInstanceId}/stage/task-check/run")
    public ResponseEntity<String> runTaskCheckStage(@PathVariable String workflowInstanceId, @RequestBody StageRunRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.runTaskCheckStage(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/stage/task-check")
    public ResponseEntity<String> getTaskCheckStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getTaskCheckStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/stage/task-check/confirm")
    public ResponseEntity<String> confirmTaskCheckStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.confirmTaskCheckStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 成果物导出 ====================

    @PostMapping("/workflows/{workflowInstanceId}/stage/result-export/run")
    public ResponseEntity<String> runResultExportStage(@PathVariable String workflowInstanceId, @RequestBody StageRunRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.runResultExportStage(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/stage/result-export")
    public ResponseEntity<String> getResultExportStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getResultExportStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/stage/result-export/deliverables")
    public ResponseEntity<String> getDeliverables(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getDeliverables(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/stage/result-export/deliverables/{tableKey}")
    public ResponseEntity<String> getDeliverableByKey(@PathVariable String workflowInstanceId, @PathVariable String tableKey, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getDeliverableByKey(workflowInstanceId, tableKey, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/stage/result-export/confirm")
    public ResponseEntity<String> confirmResultExportStage(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.confirmResultExportStage(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/confirmed-results")
    public ResponseEntity<String> getConfirmedResults(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getConfirmedResults(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 导出文件 ====================

    @PostMapping("/workflows/{workflowInstanceId}/exports/files")
    public ResponseEntity<String> generateExportFiles(@PathVariable String workflowInstanceId, @RequestBody ExportFilesRequest request, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.generateExportFiles(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/exports")
    public ResponseEntity<String> getExports(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getExports(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 对象表与人工复核 ====================

    @GetMapping("/workflows/{workflowInstanceId}/objects")
    public ResponseEntity<String> getObjects(
            @PathVariable String workflowInstanceId,
            @RequestParam(value = "object_type", required = false) String objectType,
            @RequestParam(value = "parent_object_type", required = false) String parentObjectType,
            @RequestParam(value = "parent_object_id", required = false) String parentObjectId,
            HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getObjects(workflowInstanceId, objectType, parentObjectType, parentObjectId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/objects/sync")
    public ResponseEntity<String> syncObjects(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.syncObjects(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/review-items")
    public ResponseEntity<String> getReviewItems(
            @PathVariable String workflowInstanceId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "related_stage", required = false) String relatedStage,
            HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getReviewItems(workflowInstanceId, status, priority, relatedStage, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/review-items/{reviewItemId}/action")
    public ResponseEntity<String> actionReviewItem(
            @PathVariable String workflowInstanceId,
            @PathVariable String reviewItemId,
            @RequestBody ReviewItemActionRequest request,
            HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.actionReviewItem(workflowInstanceId, reviewItemId, request, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 操作日志 ====================

    @GetMapping("/workflows/{workflowInstanceId}/logs")
    public ResponseEntity<String> getLogs(@PathVariable String workflowInstanceId, HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getLogs(workflowInstanceId, userId);
        return ResponseEntity.ok(result);
    }

    // ==================== 试点版：标准化助手 ====================

    @GetMapping("/workflows/{workflowInstanceId}/standardization/monthly-todos")
    public ResponseEntity<String> getMonthlyTodos(
            @PathVariable String workflowInstanceId,
            @RequestParam(value = "month", required = false) String month,
            HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getMonthlyTodos(workflowInstanceId, month, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/standardization/multi-form-package")
    public ResponseEntity<String> getMultiFormPackage(
            @PathVariable String workflowInstanceId,
            @RequestParam(value = "month", required = false) String month,
            HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getMultiFormPackage(workflowInstanceId, month, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/workflows/{workflowInstanceId}/standardization/assistant")
    public ResponseEntity<String> getStandardizationAssistant(
            @PathVariable String workflowInstanceId,
            @RequestParam(value = "month", required = false) String month,
            HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.getStandardizationAssistant(workflowInstanceId, month, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/workflows/{workflowInstanceId}/standardization/files")
    public ResponseEntity<String> generateStandardizationFiles(
            @PathVariable String workflowInstanceId,
            @RequestBody StandardizationFilesRequest request,
            HttpServletRequest httpRequest) {
        String userId = getUserId(httpRequest);
        String result = riskAssessmentService.generateStandardizationFiles(workflowInstanceId, request, userId);
        return ResponseEntity.ok(result);
    }
}