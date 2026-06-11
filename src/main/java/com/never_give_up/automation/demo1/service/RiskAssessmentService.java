package com.never_give_up.automation.demo1.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.never_give_up.automation.demo1.config.RiskAssessmentProperties;
import com.never_give_up.automation.demo1.dto.request.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAssessmentService {

    private final RiskAssessmentProperties properties;

    private Map<String, String> buildHeaders() {
        Map<String, String> headers = MapUtil.newHashMap();
        headers.put("X-API-Key", properties.getApiKey());
        return headers;
    }

    private Map<String, String> buildHeadersWithUserId(String userId) {
        Map<String, String> headers = buildHeaders();
        headers.put("X-User-Id", userId);
        return headers;
    }

    private String getUrl(String path) {
        return properties.getBaseUrl() + path;
    }

    private String doGet(String url, Map<String, String> headers) {
        log.debug("GET: {}", url);
        HttpResponse response = HttpRequest.get(url)
                .addHeaders(headers)
                .timeout(30000)
                .execute();
        log.debug("Response: {}", response.body());
        return response.body();
    }

    private String doPost(String url, String body, Map<String, String> headers) {
        log.debug("POST: {}, body: {}", url, body);
        HttpResponse response = HttpRequest.post(url)
                .body(body)
                .addHeaders(headers)
                .timeout(30000)
                .execute();
        log.debug("Response: {}", response.body());
        return response.body();
    }

    private String doPatch(String url, String body, Map<String, String> headers) {
        log.debug("PATCH: {}, body: {}", url, body);
        HttpResponse response = HttpRequest.patch(url)
                .body(body)
                .addHeaders(headers)
                .timeout(30000)
                .execute();
        log.debug("Response: {}", response.body());
        return response.body();
    }

    private String doDelete(String url, Map<String, String> headers) {
        log.debug("DELETE: {}", url);
        HttpResponse response = HttpRequest.delete(url)
                .addHeaders(headers)
                .timeout(30000)
                .execute();
        log.debug("Response: {}", response.body());
        return response.body();
    }

    private String doPostMultipart(String url, Map<String, Object> formData, Map<String, String> headers) {
        try {
            HttpRequest request = HttpRequest.post(url);
            request.addHeaders(headers);

            for (Map.Entry<String, Object> entry : formData.entrySet()) {
                if (entry.getValue() instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) entry.getValue();
                    // 使用 byte[] 方式上传，避免临时文件问题
                    byte[] fileBytes = file.getBytes();
                    request.form(entry.getKey(), fileBytes, file.getOriginalFilename());
                } else if (entry.getValue() != null) {
                    request.form(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }

            HttpResponse response = request.timeout(60000).execute();
            return response.body();
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    // ==================== 风险评估实例相关 ====================

    public String createWorkflow(CreateWorkflowRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows"), body, headers);
    }

    public String getWorkflowByExternalTask(String externalTaskId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/by-external-task/" + externalTaskId), headers);
    }

    public String getWorkflowDetail(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId), headers);
    }

    public String updateWorkflow(String workflowInstanceId, UpdateWorkflowRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPatch(getUrl("/api/workflows/" + workflowInstanceId), body, headers);
    }

    // ==================== 资料相关 ====================

    public String uploadMaterial(String workflowInstanceId, UploadMaterialRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        Map<String, Object> formData = new HashMap<>();
        formData.put("file", request.getFile());
        formData.put("material_type", request.getMaterialType());
        formData.put("uploaded_by", request.getUploadedBy());
        formData.put("remark", request.getRemark());
        return doPostMultipart(getUrl("/api/workflows/" + workflowInstanceId + "/materials"), formData, headers);
    }

    public String getMaterials(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/materials"), headers);
    }

    public String updateMaterial(String workflowInstanceId, String materialId, Map<String, Object> request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPatch(getUrl("/api/workflows/" + workflowInstanceId + "/materials/" + materialId), body, headers);
    }

    public String deleteMaterial(String workflowInstanceId, String materialId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doDelete(getUrl("/api/workflows/" + workflowInstanceId + "/materials/" + materialId), headers);
    }

    public String parseMaterial(String workflowInstanceId, String materialId, ParseMaterialRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/materials/" + materialId + "/parse"), body, headers);
    }

    public String difyParseMaterial(String workflowInstanceId, String materialId, DifyParseRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/materials/" + materialId + "/dify-parse"), body, headers);
    }

    public String setParsedFields(String workflowInstanceId, String materialId, Map<String, Object> request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/materials/" + materialId + "/parsed-fields"), body, headers);
    }

    // ==================== 阶段相关 ====================

    public String runMaterialParseStage(String workflowInstanceId, StageRunRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/material-parse/run"), body, headers);
    }

    public String getMaterialParseStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/material-parse"), headers);
    }

    public String confirmMaterialParseStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/material-parse/confirm?operator=" + userId), "", headers);
    }

    // ==================== 资料完整性检查 ====================

    public String runCompletenessCheckStage(String workflowInstanceId, StageRunRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/completeness-check/run"), body, headers);
    }

    public String getCompletenessCheckStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/completeness-check"), headers);
    }

    public String confirmCompletenessCheckStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/completeness-check/confirm?operator=" + userId), "", headers);
    }

    // ==================== 评估范围确认 ====================

    public String getScopeOptions(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/scope-confirm/options"), headers);
    }

    public String selectScope(String workflowInstanceId, ScopeConfirmSelectRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/scope-confirm/select"), body, headers);
    }

    public String runScopeConfirmStage(String workflowInstanceId, StageRunRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/scope-confirm/run"), body, headers);
    }

    public String getScopeConfirmStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/scope-confirm"), headers);
    }

    public String confirmScopeConfirmStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/scope-confirm/confirm?operator=" + userId), "", headers);
    }

    // ==================== 风险单元划分 ====================

    public String runRiskUnitStage(String workflowInstanceId, StageRunRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/risk-unit/run"), body, headers);
    }

    public String getRiskUnitStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/risk-unit"), headers);
    }

    public String confirmRiskUnitStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/risk-unit/confirm?operator=" + userId), "", headers);
    }

    // ==================== 风险点识别 ====================

    public String runRiskPointStage(String workflowInstanceId, StageRunRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/risk-point/run"), body, headers);
    }

    public String getRiskPointStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/risk-point"), headers);
    }

    public String confirmRiskPointStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/risk-point/confirm?operator=" + userId), "", headers);
    }

    // ==================== 危险源辨识 ====================

    public String runHazardIdentificationStage(String workflowInstanceId, StageRunRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/hazard-identification/run"), body, headers);
    }

    public String getHazardIdentificationStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/hazard-identification"), headers);
    }

    public String confirmHazardIdentificationStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/hazard-identification/confirm?operator=" + userId), "", headers);
    }

    // ==================== 风险评价 ====================

    public String runRiskEvaluationStage(String workflowInstanceId, StageRunRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/risk-evaluation/run"), body, headers);
    }

    public String getRiskEvaluationStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/risk-evaluation"), headers);
    }

    public String confirmRiskEvaluationStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/risk-evaluation/confirm?operator=" + userId), "", headers);
    }

    // ==================== 管控措施 ====================

    public String runControlMeasureStage(String workflowInstanceId, StageRunRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/control-measure/run"), body, headers);
    }

    public String getControlMeasureStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/control-measure"), headers);
    }

    public String confirmControlMeasureStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/control-measure/confirm?operator=" + userId), "", headers);
    }

    // ==================== 任务与检查项 ====================

    public String runTaskCheckStage(String workflowInstanceId, StageRunRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/task-check/run"), body, headers);
    }

    public String getTaskCheckStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/task-check"), headers);
    }

    public String confirmTaskCheckStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/task-check/confirm?operator=" + userId), "", headers);
    }

    // ==================== 成果物导出 ====================

    public String runResultExportStage(String workflowInstanceId, StageRunRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/result-export/run"), body, headers);
    }

    public String getResultExportStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/result-export"), headers);
    }

    public String getDeliverables(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/result-export/deliverables"), headers);
    }

    public String getDeliverableByKey(String workflowInstanceId, String tableKey, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/stage/result-export/deliverables/" + tableKey), headers);
    }

    public String confirmResultExportStage(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/result-export/confirm?operator=" + userId), "", headers);
    }

    public String getConfirmedResults(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/confirmed-results"), headers);
    }

    // ==================== 导出文件 ====================

    public String generateExportFiles(String workflowInstanceId, ExportFilesRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/stage/result-export/files"), body, headers);
    }

    public String getExports(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/exports"), headers);
    }

    // ==================== 对象表与人工复核 ====================

    public String getObjects(String workflowInstanceId, String objectType, String parentObjectType, String parentObjectId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        StringBuilder url = new StringBuilder(getUrl("/api/workflows/" + workflowInstanceId + "/objects"));
        boolean first = true;
        if (objectType != null && !objectType.isEmpty()) {
            url.append("?object_type=").append(objectType);
            first = false;
        }
        if (parentObjectType != null && !parentObjectType.isEmpty()) {
            url.append(first ? "?" : "&").append("parent_object_type=").append(parentObjectType);
            first = false;
        }
        if (parentObjectId != null && !parentObjectId.isEmpty()) {
            url.append(first ? "?" : "&").append("parent_object_id=").append(parentObjectId);
        }
        return doGet(url.toString(), headers);
    }

    public String syncObjects(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/objects/sync?operator=" + userId), "", headers);
    }

    public String getReviewItems(String workflowInstanceId, String status, String priority, String relatedStage, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        StringBuilder url = new StringBuilder(getUrl("/api/workflows/" + workflowInstanceId + "/review-items"));
        boolean first = true;
        if (status != null && !status.isEmpty()) {
            url.append("?status=").append(status);
            first = false;
        }
        if (priority != null && !priority.isEmpty()) {
            url.append(first ? "?" : "&").append("priority=").append(priority);
            first = false;
        }
        if (relatedStage != null && !relatedStage.isEmpty()) {
            url.append(first ? "?" : "&").append("related_stage=").append(relatedStage);
        }
        return doGet(url.toString(), headers);
    }

    public String actionReviewItem(String workflowInstanceId, String reviewItemId, ReviewItemActionRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/review-items/" + reviewItemId + "/action"), body, headers);
    }

    // ==================== 操作日志 ====================

    public String getLogs(String workflowInstanceId, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        return doGet(getUrl("/api/workflows/" + workflowInstanceId + "/logs"), headers);
    }

    // ==================== 试点版：标准化助手 ====================

    public String getMonthlyTodos(String workflowInstanceId, String month, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String url = getUrl("/api/workflows/" + workflowInstanceId + "/standardization/monthly-todos");
        if (month != null && !month.isEmpty()) {
            url += "?month=" + month;
        }
        return doGet(url, headers);
    }

    public String getMultiFormPackage(String workflowInstanceId, String month, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String url = getUrl("/api/workflows/" + workflowInstanceId + "/standardization/multi-form-package");
        if (month != null && !month.isEmpty()) {
            url += "?month=" + month;
        }
        return doGet(url, headers);
    }

    public String getStandardizationAssistant(String workflowInstanceId, String month, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String url = getUrl("/api/workflows/" + workflowInstanceId + "/standardization/assistant");
        if (month != null && !month.isEmpty()) {
            url += "?month=" + month;
        }
        return doGet(url, headers);
    }

    public String generateStandardizationFiles(String workflowInstanceId, StandardizationFilesRequest request, String userId) {
        Map<String, String> headers = buildHeadersWithUserId(userId);
        String body = JSONUtil.toJsonStr(request);
        return doPost(getUrl("/api/workflows/" + workflowInstanceId + "/standardization/files"), body, headers);
    }
}