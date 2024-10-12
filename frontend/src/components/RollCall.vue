<template>
  <div id="check-attendance">
    <div id="header">
      <button id="mode-button" @click="toggleMode"
        :style="{ backgroundColor: isRandomMode ? '#87CEEB' : '#d74e3e', borderRadius: '20px', color: 'white' }">
        {{ modeName }}
      </button>
    </div>

    <p v-if="randomEvent" style="text-align: center; font-size: 18px;">
      目前随机事件为：{{ randomEvent }}
    </p>

    <div id="attendance-button-container">
      <button id="attendance-button" :style="{ backgroundColor: isRandomMode ? '#87CEEB' : '#d74e3e' }"
        @click="getRandomStudent">
        点名
      </button>
    </div>



    <div v-if="currentStudent" id="student-info">
      <p style="font-size: 24px;">
        学号: <span style="margin-right: 50px;">{{ currentStudent.no }}</span>
        姓名: {{ currentStudent.name }}
      </p>

      <div id="action-buttons">
        <button id="absentBtn" @click="markAbsent">没到教室</button>
        <button id="presentBtn" @click="markPresent">到达教室</button>
        <button id="transferBtn" @click="transferAttendance">转移点名</button>
      </div>
    </div>
  </div>
</template>

<script>
import { GetRollBackStudent, ModifyStudentScore } from '@/api/api';

export default {
  data() {
    return {
      modeName: '生成随机事件',
      isRandomMode: true, /* true为正常模式，false为随机事件模式 */
      detailModeType: 0, /* 0为权重模式，1为众生平等，2为两倍积分模式，3为幸运星期三模式 */
      currentStudent: null,
      students: [],
      randomEvent: null, // 新增属性用于存储随机事件
      randomEvents: ['双倍扣分', '幸运星期三', '众生平等'], // 随机事件列表
    };
  },
  methods: {
    toggleMode() {
      this.isRandomMode = !this.isRandomMode;
      this.modeName = this.isRandomMode ? '生成随机事件' : '返回正常模式';

      if (!this.isRandomMode) {
        this.randomEvent = this.randomEvents[Math.floor(Math.random() * this.randomEvents.length)]; // 随机生成事件
        if (this.randomEvent == '双倍扣分') {
          this.detailModeType = 2;
        } else if (this.randomEvent == '幸运星期三') {
          this.detailModeType = 3;
        } else if (this.randomEvent == '众生平等') {
          this.detailModeType = 1;
        }
      } else {
        this.randomEvent = null; // 退出随机模式时清空事件
      }
    },
    getRandomStudent() {
      GetRollBackStudent(
        this.detailModeType
      ).then((res) => {
        if (res.code == 500) {
          this.$message.error("获取点名学生发生错误");
        } else if (res.code == 200) {
          this.currentStudent = res.data;
          this.$message.success("查询成功");
        } else if (res.code == 401) {
          alert("登录过期，请重新登录！");
          if (this.$route.path !== "/login") {
            this.$router.push("/login");
          }
        }
      });
    },
    markAbsent() {
      let score = -2;
      if (this.detailModeType == 2) {
        score = -4;
      }
      ModifyStudentScore(this.currentStudent.id, score).then((res) => {
        if (res.code == 500) {
          this.$message.error("修改分数发生错误");
          this.currentStudent = null; // 清空当前学生
        } else if (res.code == 200) {
          if (score == -2) {
            alert(`${this.currentStudent.name} 未到教室，已被登记，扣除两分`);
          } else {
            alert(`${this.currentStudent.name} 未到教室，已被登记，扣除四分`);
          }

          this.currentStudent = null; // 清空当前学生
        } else if (res.code == 401) {
          alert("登录过期，请重新登录！");
          if (this.$route.path !== "/login") {
            this.$router.push("/login");
          }
        }
      });
    },
    markPresent() {
      let score = 1;
      if (this.detailModeType == 3) {
        score = score * 3;
      }
      ModifyStudentScore(this.currentStudent.id, score).then((res) => {
        if (res.code == 500) {
          this.$message.error("修改分数发生错误");
          this.currentStudent = null; // 清空当前学生
        } else if (res.code == 200) {
          this.$message.success(this.currentStudent.name + " 到达教室增加"+score+"分，进入提问环节");
          if (this.$route.path !== "/question") {
            this.$router.push({ name: 'question', params: { student: this.currentStudent, detailModeType: this.detailModeType } });
          }
        } else if (res.code == 401) {
          alert("登录过期，请重新登录！");
          if (this.$route.path !== "/login") {
            this.$router.push("/login");
          }
        }
      });
    },
    transferAttendance() {
      alert(`${this.currentStudent.name} 转移点名，重新进行点名`);
      this.currentStudent = null; // 清空当前学生
    },
  },
};
</script>

<style scoped>
#check-attendance {
  box-sizing: border-box;
  position: relative;
  width: 85.4%;
  height: 100vh;
  /* background-image: url("../assets/background.png"); */
  background-image: url("../assets/login/login-background.png");
  background-size: cover;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  overflow: hidden;
  /* 隐藏溢出内容 */
}

#header {
  display: flex;
  justify-content: flex-end;
  margin: 10px;
  height: 50px;
}

#mode-button {
  border: none;
  border-radius: 20px;
  /* 圆角按钮 */
  padding: 10px 20px;
  /* 增加内边距 */
  font-size: 16px;
  /* 调整字体大小 */
  cursor: pointer;
  /* 鼠标指针样式 */
}

#attendance-button-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 10px;
}

#attendance-button {
  border: none;
  border-radius: 50%;
  width: 100px;
  height: 100px;
  font-size: 20px;
  color: white;
  margin: 10px;
}

#student-info {
  text-align: center;
  margin-top: 0;
  padding-top: 0;
}

#action-buttons {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

#action-buttons button {
  flex: 0 0 100px;
  /* 增加宽度 */
  height: 70px;
  /* 增加高度 */
  margin: 0 15px;
  /* 增加按钮之间的间距 */
  border-radius: 10px;
  font-size: 18px;
  /* 增加字体大小 */
}


#absentBtn {
  background-color: #d74e3e;
  /* 警告红色 */
  color: white;
  border: none;
}

#presentBtn {
  background-color: #87CEEB;
  /* 蓝色 */
  color: white;
  border: none;
}

#transferBtn {
  background-color: #FFD700;
  /* 黄色 */
  color: white;
  border: none;
}
</style>
