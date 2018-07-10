package pers.zc.activiti;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.OutputStream;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private ProcessEngine processEngine;

	/**
	 * 部属流程
	 * @author kawin
	 * @date 2018/7/10 17:15
	 * @param []
	 * @return void
	 */

	@Test
	public void deploy() {
		Deployment deployment = repositoryService.createDeployment()
				.addClasspathResource("claimCompany.bpmn")
				.name("请假流程单1")
				.category("办公类别")
				.deploy();
		System.out.println("部署的id"+deployment.getId());
		System.out.println("部署的名称"+deployment.getName());
	}

	/**
	 * 根据流程ID获取到流程实例 执行请假流程 流到申请请假环节
	 * @author kawin
	 * @date 2018/7/10 17:14
	 * @param []
	 * @return void
	 */
	@Test
	public void startProcess() {
		//拿到流程图的Id
		String processDefikey = "claimCompany";
		//获取到流程实例
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefikey);
		System.out.println("流程实例id:"+processInstance.getId());//流程实例id
		System.out.println("流程定义id:"+processInstance.getProcessDefinitionId());//输出流程定义的id

	}
	/**
	 * 查询申请请假环节 任务
	 * @author kawin
	 * @date 2018/7/10 17:14
	 * @param []
	 * @return void
	 */
	@Test
	public void queryTask(){
		//任务的办理人
		String assignee = "班主任";
		//创建一个任务查询对象
		TaskQuery taskQuery = taskService.createTaskQuery();
		List<Task> list = taskQuery.taskAssignee(assignee).list();
		//遍历任务列表
		if (list!=null&&list.size()>0){
			for (Task task : list){
				System.out.println("任务的办理人:"+task.getAssignee());
				System.out.println("任务的Id:"+task.getId());
				System.out.println("任务的名称:"+task.getName());
			}
		}
	}

	/**
	 * 完成提交申请请假的环节
	 * @author kawin
	 * @date 2018/7/10 17:14
	 * @param []
	 * @return void
	 */
	@Test
	public void compileTask1(){
		//提交申请的任务id
		String taskId = "15005";
		taskService.complete(taskId);
		System.out.println("完成提交申请请假的环节");
	}
	/**
	 *
	 * 完成班主任审批环节
	 *
	 * @author kawin
	 * @date 2018/7/10 17:13
	 * @param []
	 * @return void
	 */

	@Test
	public void compileTask2(){
		//班主任任务Id
		String taskId = "15005";
		taskService.complete(taskId);
		System.out.println("完成班主任审批环节");
	}
	/**
	 *
	 * 完成老师审批环节
	 * @author kawin
	 * @date 2018/7/10 17:13
	 * @param []
	 * @return void
	 */

	@Test
	public void compileTask3(){
		//老师任务Id
		String taskId = "15005";
		taskService.complete(taskId);
		System.out.println("完成老师审批环节");
	}
	/**
	 *  获取流程的状态
	 *
	 * @author kawin
	 * @date 2018/7/10 17:13
	 * @param []
	 * @return void
	 */

	@Test
	public void getProcessInstanceState(){
		//拿到流程实体的ID
		String processInstanceId = "15001";
		ProcessInstance pi = processEngine.getRuntimeService()
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId)
				.singleResult();
		if (pi!=null){
			System.out.println("该流程实例"+processInstanceId+"正在运行...  "+"当前活动的任务:"+pi.getActivityId());
		}else {
			System.out.println("当前的流程实例"+processInstanceId+" 已经结束！");
		}
	}

	/**
	 *
	 * 查询历史流程实例
	 * @author kawin
	 * @date 2018/7/10 17:10
	 * @return void
	 */
	@Test
	public void queryHistoryProInstance(){
		List<HistoricProcessInstance> list = processEngine.getHistoryService()
				.createHistoricProcessInstanceQuery()
				.list();
		if (list!=null&&list.size()>0){
			for (HistoricProcessInstance historicProcessInstance : list){
				System.out.println("历史流程实例的Id:"+historicProcessInstance.getId());
				System.out.println("历史流程定义的Id:"+historicProcessInstance.getProcessDefinitionId());
				System.out.println("历史流程开始时间--结束时间:"+historicProcessInstance.getStartTime()+"--"+historicProcessInstance.getEndTime());
			}
		}
	}

	/**
	 *
	 * 查询历史实例执行任务信息
	 * @author kawin
	 * @date 2018/7/10 17:21
	 * @param []
	 * @return void
	 */
	@Test
	public void queryHistoryTask(){
		String processInstanceId = "15001";
		List<HistoricTaskInstance> list = processEngine.getHistoryService()
				.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId)
				.list();
		if (list.size()>0){
			for (HistoricTaskInstance historicTaskInstance : list){
				System.out.print("历史流程实例任务id:"+historicTaskInstance.getId());
				System.out.print("历史流程定义的id:"+historicTaskInstance.getProcessDefinitionId());
				System.out.print("历史流程实例任务名称:"+historicTaskInstance.getName());
				System.out.println("历史流程实例任务处理人:"+historicTaskInstance.getAssignee());
			}
		}

	}

}
