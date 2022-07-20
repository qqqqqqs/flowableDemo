package com.feiyu.controller;


import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
public class ProcessController {
    private final RuntimeService runtimeService;

    private final IdentityService identityService;

    public ProcessController(RuntimeService runtimeService, IdentityService identityService) {
        this.runtimeService = runtimeService;
        this.identityService = identityService;
    }

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;





    @PostMapping("/start")
    @Transactional(rollbackFor = Exception.class)
    public String start(@RequestParam String key) {
        try {
            // 设置流程启动用户
            identityService.setAuthenticatedUserId("1");
            //根据key启动（还可以根据ID、TENANT_ID_启动），返回流程实例（正在运行的流程）
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);
            String processInstanceId = processInstance.getProcessInstanceId();
            //返回流程实例ID
            return processInstanceId;
        } catch (Exception e) {
            e.printStackTrace();
            return "启动失败！";
        }
    }





    /**获取指定用户的待办的任务列表
     * 代办
     * */
    @GetMapping("/getNeedDoTasks")
    @Transactional(rollbackFor = Exception.class)
    public String getNeedDoTasks() {
        //设当前登录用户ID
        String userId = "1";

        //taskCandidateOrAssigned 表示不管是任务的候选人还是直接分配的人都可以查到
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned(userId)
                .list();

        if (!tasks.isEmpty()) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (Task task : tasks) {
                Map<String, Object> map = new HashMap<>();
                //代办id
                String taskId = task.getId();
                //代办节点名称
                String taskName = task.getName();
                //创建时间
                Date createTime = task.getCreateTime();
                map.put("taskId", taskId);
                map.put("taskName", taskName);
                map.put("createTime", createTime);
                list.add(map);
            }
            return listToString(list,',');
        }

        return "当前用户在当前流程实例中并无待办！";
    }



    // 列表转字符串
    public String listToString(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(separator);
        }
        return sb.toString().substring(0,sb.toString().length()-1);
    }

    /**审批通过并跳转流程*/
    @GetMapping("/audit")
    @Transactional(rollbackFor = Exception.class)
    public String audit(@RequestParam String taskId) {
        try {
            //跳转流程
            taskService.complete(taskId);
            return "已审批成功！！！！！！！！ 走监听就不一样";
        } catch (Exception e) {
            e.printStackTrace();
            return "跳转失败！";
        }
    }
    /**审批通过并跳转流程*/
    @GetMapping("/shenhe")
    @Transactional(rollbackFor = Exception.class)
    public String shenhe(@RequestParam String taskId) {
        try {
            //跳转流程
            taskService.complete(taskId);
            return "已审批";
        } catch (Exception e) {
            e.printStackTrace();
            return "跳转失败！";
        }
    }


    /**
     * 获取已办任务列表
     * */
    @GetMapping("/getAlreadyDoTasks")
    @Transactional(rollbackFor = Exception.class)
    public String getAlreadyDoTasks() {
        //获取当前登录用户ID
        long userId = 2L;

        List<HistoricTaskInstance> taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .includeProcessVariables()
                .finished()
                .taskAssignee(userId+"")
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .list();

        if (!taskInstanceQuery.isEmpty()) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (HistoricTaskInstance task : taskInstanceQuery) {
                Map<String, Object> map = new HashMap<>();
                //代办id
                String taskId = task.getId();
                //代办节点名称
                String taskName = task.getName();
                //创建时间
                Date createTime = task.getCreateTime();
                map.put("taskId", taskId);
                map.put("taskName", taskName);
                map.put("createTime", createTime);
                list.add(map);
            }
            return listToString(list,',');
        }
        return "暂无已办";
    }




}
