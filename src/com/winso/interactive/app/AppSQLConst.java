package com.winso.interactive.app;

public class AppSQLConst {

	//公告
	public static final String S_SQL_GET_NOTICE = "select top 40 * from notice order by update_time desc";
	public static final String S_SQL_GET_NOTICE_DETAIL = "select   * from notice where notice_id=:notice_id<int>";
	
	
	//登录
	public static final String S_SQL_LOGIN = "select * from t_user where  login_name=:login_name<char[60]> and pwd=:pwd<char[60]>";
	
	//插入考勤
	public static final String S_SQL_INSERT_ATTENDANCE="insert into attendance(student_id,cource_id,create_time,absent) values(:student_id<int>,:cource_id<int>,GETDATE(),1)	";
	
	//是否已经签到
	public static final String S_SQL_ATANDANCE_IS_ANSANCE = "select top 1 create_time,cource_id from attendance where create_time > GETDATE()-0.02 and  student_id=:studentID<int>  order by  create_time desc";
	
	//是否已经签到
	public static final String S_SQL_LEAVE_IS_LEAVE = "select 1 from leave where from_time <=getdate()  and to_time >= getdate() and student_id=:studentID<int> ";

		
	//查询请假历史
	//是否已经签到
	public static final String S_SQL_GET_LEAVE_HIS= "select top 20 leave_id as title_id,b.name as title_left,content as title_content ,CONVERT(varchar(16), a.create_time, 120 ) as title_right,a.to_time  from leave a,t_user b  where a.create_time > GETDATE()-300 and a.teacher_id =b.user_id and  student_id=:studentID<int>  order by  create_time desc";

	//获取评价项的评分
	public static final String S_SQL_GET_ITEM_GRADE_ID = "select grade_id from evaluation_item where item_id=:item_id<int>";
	
	
	//获取评价项的评分
	public static final String S_SQL_GET_LAST_COURSE = "select top 1 * from t_course_log where from_time <=getDate()+1 and class_id in ( select class_id from t_user where user_id=:user_id<int> )  order by from_time desc";

			
	//获取教师的列表
	public static final String S_SQL_GET_TEACHER_LIST= "select top 100 user_id as title_id,b.department_name as title_left,c.title_name , name as title_right from t_user a,department b,title c  where a.department_id=b.department_id and a.title_id=c.title_id and is_teacher=1";
	
	
	//获取同班的学生列表
	public static final String S_SQL_GET_STUDENT_LIST= "select top 100 user_id as title_id,'' as title_right,name as title_left  from t_user a where  (is_teacher=0 or is_teacher is null) and a.class_id in ( select class_id from t_user where user_id=:user_id<int>)";

	//获取同班的学生列表
	public static final String S_SQL_GET_STUDENT_LIST_NOT_REVIEW= "select top 100 user_id as title_id,'' as title_right,name as title_left  from t_user a where  (is_teacher=0 or is_teacher is null) and a.class_id in ( select class_id from t_user where user_id=:user_id<int>) and user_id<>:user_id2<int> ";  // 

	//查找某课已评阅的学生
	public static final String S_SQL_GET_STUDENT_HAS_REVIEW = "select distinct be_user_id from evaluation_result a where a.course_id=:course_id<int> and user_id=:user_id<int> ";
	
			
	//判断是否已经评价过
	public static final String S_SQL_GET_IS_GRADE= "SELECT count(1) as count_size  FROM  evaluation_reuslt where item_id=:item_id<int> and course_id=:course_id<int>";

	
	//获取项目的评级
	public static final String S_SQL_GET_ITEM_GRADE_LIST= "SELECT a.grade_id as title_id,'' as title_right, b.grade_desc as title_left FROM  evaluation_item_grade a ,evaluation_grade b  where a.grade_id=b.grade_id and a.item_id=:item_id<int>";

	
	//获取互评项目列表
	public static final String S_SQL_GET_ITEM_LIST= "select  item_id as title_id,'' as title_right,content as title_left  from evaluation_item";

	//获取学习待评的列表
	public static final String S_SQL_GET_STUDENT_REVIEW_LIST="select evaluation_item.item_id as title_id,evaluation_item.content as title_eft,evaluation_result.grade_id as title_right   from evaluation_item  left join evaluation_result   on evaluation_result.item_id=evaluation_item.item_id and  evaluation_result.course_id=:course_id<int>  and evaluation_result.be_user_id=:user_id<int> ";
	
	
	//保存互评
	//evaluation_reuslt
	public static final String S_SQL_INSERT_EVALUATION_RESULT="insert into evaluation_result(item_id,user_id,course_id,evaluation_type,grade_id,evaluation_info,create_time,be_user_id,be_group_id ) values (:item_id<int>,:user_id<int>,:course_id<int>,:evaluation_type<int>,:grade_id<int>,:evaluation_info<char[1000]>,GetDate(),:be_user_id<int>,:be_group_id<int> ) ";

	//保存请假
	public static final String S_SQL_INSERT_LEAVE="insert into leave(student_id,teacher_id,content,from_time,to_time) values(:student_id<int>,:teacher_id<int>,:content<char[2000]>,:from_time<char[40]>,:to_time<char[40]>)";
	
	//获取详细的Leave信息
	public static final String S_SQL_GET_LEAVE_DETAIL="select a.*,b.name as teacher_name from leave a,t_user b where a.teacher_id=b.user_id and leave_id=:leave_id<int>";
	
	
	//获取详细的信息,所属的教师的编号
	public static final String S_SQL_GET_REVIEW_TEACHER_NAME="select user_id,name as review_teacher_name from t_user where user_id=:user_id1<int> or user_id=:user_id2<int>";

	//查询最新的问题
	//public static final String S_SQL_GET_LAST_ASK="SELECT a.ask_id,a.ask_time,a.ask_type,a.cource_id,a.teacher_id,b.*  FROM  ask_log a,test_item b where cource_id=:cource_id<int> and  ask_time > getDate()-0.042 and a.item_id=b.item_id";
	public static final String S_SQL_GET_LAST_ASK="SELECT top 1 a.ask_id,a.ask_time,a.ask_type,a.cource_id,a.teacher_id,b.*  FROM  ask_log a,test_item b where cource_id=:cource_id<int> and  ask_time > getDate()-33.042 and a.item_id=b.item_id order by ask_time desc";
	
	
	public static final String S_SQL_INSERT_ANSWER="insert into answer_log(ask_id,student_id,group_id,answer_time,is_a,is_b,is_c,is_d,is_e,is_f,is_g,answer_content,is_right,pic_draw) values ( :ask_id<int>,:student_id<int>,:group_id<int>,GetDate(),:is_a<int>,:is_b<int>,:is_c<int>,:is_d<int>,:is_e<int>,:is_f<int>,:is_g<int>,:answer_content<char[2000]>,:is_right<int>,:pic_draw<char[2000]> ) ";
	
	
	//上报IP地址
	public static final String S_SQL_UPDATE_USER_IP="update t_user set ip=:ipaddr<char[60]> where user_id=:user_id<int> ";
	
	//用户网络变更
	public static final String S_SQL_CHANGE_NET_USER_IP="insert into t_user_net_log(user_id,cur_ip,pre_ip,create_time,change_type,comment) values (:user_id<int>,:cur_ip<char[60]>,:pre_ip<char[60]>,GetDate(),:change_type<int>,:comment<char[150]>)";

	
	//抢答
	public static final String S_SQL_ROB_ASK="{call pr_rob_ask(:user_id<int,in>,:ask_id<int,in>,:rand_id<char[40],in>)}";

	
	// 先查找所有本学生的item
	//	select top 100  '' as grade_desc   , user_id as title_id,phone as title_right,name as title_left  
	//	from t_user a where  (is_teacher=0 or is_teacher is null) and a.class_id in 
	//	( select class_id from t_user where user_id=1)   
	//
	//  更新已经评审过的状态
	//	select distinct a.item_id,a.user_id,a.course_id,a.grade_id,b.grade_desc 
	//	from evaluation_result a,evaluation_grade b where a.grade_id=b.grade_id and user_id=1 and a.course_id=79

	
}
