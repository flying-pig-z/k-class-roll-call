<template>
  <div id="student">
    <div id="search-box">


      <div class="search">
        学号：
        <input type="text" id="number" v-model="no" @keyup.enter="search" />
      </div>

      <div class="search">
        姓名：
        <input type="text" id="number" v-model="name" @keyup.enter="search" />
      </div>

      <button id="searchbtn" @click="search">搜索</button>
      <el-upload
        class="upload-demo"
        action="http://localhost:9090/student/import"  
        :before-upload="beforeUpload"
        :on-success="handleSuccess"
        :on-error="handleError"
        :show-file-list="false"
        accept=".xls,.xlsx" 
      >
        <el-button id="importbtn" type="primary">导入学生名单</el-button>
      </el-upload>


    </div>

    <div id="banner">
      <span id="span-no">学生学号</span>
      <span id="span-name">姓名</span>
      <span id="span-major">专业</span>
      <span id="span-score">积分</span>
      <span id="delete">删除按钮</span>
    </div>
    <div id="list">
      <div class="list-item" v-for="(student, i) in pagedOrders" :key="i">
        <span>{{ student.no }}</span>
        <span>{{ student.name }}</span>
        <span>{{ student.major }}</span>
        <span>{{ student.score }}</span>
        <!-- 按下修改按钮，将索引 i 传递给方法 -->
        <el-button type="danger" icon="el-icon-delete" circle  @click="deleteStudent(student.id)" ></el-button>
      </div>


    </div>
    <el-pagination
      id="page"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[1, 2, 5, 10]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="studentList.length"
    >
    </el-pagination>
  </div>
</template>
    
<script>
import { DeleteStudent, StudentSearch } from "@/api/api";
export default {
  name: "student",
  data() {
    return {
      id: "",
      no: "",
      name: "",
      course: "",
      courseList: [],
      studentList: [],
      currentPage: 1,
      pageSize: 5,
      fileList: [] 

    };
  },
  methods: {
    handleSizeChange(val) {
      this.pageSize = val;
    },
    handleCurrentChange(val) {
      this.currentPage = val;
    },
    beforeUpload(file) {
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || file.type === 'application/vnd.ms-excel';
      if (!isExcel) {
        this.$message.error('只能上传 Excel 文件!');
      }
      return isExcel; // 返回 false 会阻止文件上传
    },
    handleSuccess(response, file) {
      this.$message.success('文件上传成功');
    },
    handleError(err, file) {
      this.$message.error('文件上传失败');
    },
    search() {
      StudentSearch({
        no: this.no,
        name: this.name,
        pageSize: 10000,
        pageNo: 1,
      }).then((res) => {
        if (res.code == 500) {
          this.$message.error("请确保信息填写完整");
        } else if (res.code == 200) {
          if (res.data.total == 0) {
            this.$message.error("没有查询到数据");
          } else {
            this.studentList = res.data.records;
            console.log(this.studentList)
            this.$message.success("查询成功");
          }
        } else if(res.code == 401){
          alert("登录过期，请重新登录！");
          this.$router.push("/login");
        }
      });
    },
    deleteStudent(studentId) {
      DeleteStudent(studentId) // 直接传递 studentId
        .then((res) => {
          if (res.code == 500) {
            this.$message.error("删除失败");
          } else if (res.code == 200) {
              this.$message.success("删除成功");
              // 这里可以在成功删除后更新学生列表
              this.studentList.splice(index, 1); // 从学生列表中移除已删除的学生
          }
        })
        .catch((error) => {
          console.error(error); // 打印错误信息
          this.$message.error("请求失败，请稍后重试");
        });
    }

  },

  computed: {
    pagedOrders() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      const endIndex = startIndex + this.pageSize;
      return this.studentList.slice(startIndex, endIndex);
    },
  },
};
</script>
    
<style scoped>
#student {
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
  justify-content: flex-start;
}
#search-box {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: row;
  justify-content: center;
}


.search {
  width: 20%;
  margin-left: 20px;
  font-size: 15px;
  font-weight: bold;
}
#number {
  width: 70%;
  height: 40px;
  border-radius: 10px;
  border: 1px solid #ccc;
  margin-top: 50px;
  opacity: 0.7;
  font-size: 15px;
  text-align: center;
}
#searchbtn {
  width: 80px;
  height: 40px;
  border-radius: 10px;
  border: 0px;
  margin-top: 50px;
  margin-left: 20px;
  opacity: 0.74;
  font-size: 15px;
  font-weight: bold;
  background: #229fe0;
}
#searchbtn:hover {
  background: #1e90ff;
}
#searchbtn:active {
  background: #1c86ee;
}
#importbtn {
  width:150px;
  height: 40px;
  border-radius: 10px;
  border: 0px;
  margin-top: 50px;
  margin-left: 20px;
  opacity: 0.74;
  font-size: 15px;
  font-weight: bold;
  background: #229fe0;
  color: black;
}
#importbtn:hover {
  background: #1e90ff;
}
#importbtn:active {
  background: #1c86ee;
}
#banner {
  box-sizing: border-box;
  position: absolute;
  margin-left: 5.5%;
  margin-top: 125px;
  width: 90%;
  height: 50px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  background: #70707055;
  justify-content: space-evenly;
}
#span-no {
  margin: 0 0 0 35.5px;
}
#span-name {
  margin: 0 0 0 60px;
}
#span-major {
  margin: 0 0 0 50px;
}
#span-score {
  margin: 0 15px 0 50px;
}
#delete {
  margin: 0 30px 0 15px;
}
#list {
  box-sizing: border-box;
  position: absolute;
  margin-left: 5.5%;
  margin-top: 200px;
  width: 90%;
  height: 600px;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  background: transparent;
}
.list-item {
  box-sizing: border-box;
  margin-top: 10px;
  width: 100%;
  height: 50px;
  border-radius: 20px;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  background: #fffffff5;
  box-shadow: 0 -3px 3px 0 #d4d2d2 inset;
}
.list-item:hover {
  background: #95daff;
}
#page {
  margin: auto;
  margin-top: -80px;
}
</style>