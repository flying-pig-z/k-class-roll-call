<template>
    <div id="check-attendance">
    <!-- 添加 "进入提问环节" 文本 -->
    <div id="question-phase">
      进入提问环节
    </div>

    <!-- 显示正在回答问题的学生姓名 -->
    <div id="current-student">
      现在回答问题的是 {{ currentStudent.name || "某某同学" }}
    </div>

      
      <div id="circle-container">
        <button 
          v-for="(score, index) in scores" 
          :key="index" 
          class="circle-button" 
          :style="getButtonStyle(index)" 
          @click="selectScore(score)"
        >
          {{ score }}
        </button>
      </div>
      <div id="selected-score">
        选择的分数: {{ selectedScore }}
      </div>
    </div>
  </template>
  
  <script>
  import { ModifyStudentScore } from "@/api/api"; // 引入修改分数接口
  
  export default {
    data() {
      return {
        scores: [-1, 0.5, 1, 1.5, 2, 2.5, 3],
        selectedScore: null,
        currentStudent: this.$route.params.student, // 从路由参数中获取当前学生信息
        detailModeType: this.$route.params.detailModeType // 从路由参数中获取当前学生信息
      };
    },
    methods: {
        selectScore(score) {
            this.selectedScore = score; // 显示选中的分数
            if (this.detailModeType != 0) {
                score = this.detailModeType * score;
            }
            // 使用 this.detailModeType 访问 detailModeType
            ModifyStudentScore(this.currentStudent.id, score).then((res) => {
                if (res.code == 500) {
                    this.$message.error("修改分数发生错误");
                } else if (res.code == 200) {
                  this.$message.success(this.currentStudent.name + "分数修改成功, 增加" + score + "分");
                  this.$router.push("/roll-call");
                } else if (res.code == 401) {
                    alert("登录过期，请重新登录！");
                    this.$router.push("/login");
                }
            });
        },

      getButtonStyle(index) {
        const angle = (index / this.scores.length) * 2 * Math.PI; // 计算每个按钮的角度
        const radius = 150; // 圆环的半径
        const x = radius * Math.cos(angle); // 按钮的 x 坐标
        const y = radius * Math.sin(angle); // 按钮的 y 坐标
    
        return {
          position: 'absolute',
          left: `${150 + x}px`, // 将 x 坐标平移至容器中心
          top: `${150 + y}px`,  // 将 y 坐标平移至容器中心
        };
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
    min-width: 910px;
    min-height: 700px;
    background-image: url("../assets/background.png");
    background-size: cover;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
  #question-phase {
    font-size: 28px;
    color: white;
    margin-bottom: 10px;
  }
  #current-student {
    font-size: 24px;
    color: white;
    margin-bottom: 30px;
  }
  #circle-container {
    position: relative;
    width: 300px; /* 容器宽度，圆环直径 */
    height: 300px;
    position: relative;
  }
  .circle-button {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background-color: #87CEEB;
    color: white;
    border: none;
    font-size: 18px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  .circle-button:hover {
    background-color: #1E90FF;
  }
  
  #selected-score {
    margin-top: 20px;
    font-size: 24px;
    color: white;
  }
  </style>
  