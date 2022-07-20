package com.feiyu.Listener;

import org.flowable.engine.impl.el.FixedValue;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * 实现任务事件监听类
 */
@Component
public class TaskEvent implements TaskListener {

    /**
     * 名称与流程定义中的参数名称一致
     */
    private FixedValue message;


    @Override
    public void notify(DelegateTask delegateTask) {

        String val = message.getExpressionText();
        System.out.println("监听到任务流程变化");
        System.out.println("val = " + val);
    }
}
