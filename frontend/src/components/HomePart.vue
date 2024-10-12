<template>
  <div id="home-part">
    <div id="banner">
      <div id="info-time">
        <div class="title">当前时间：</div>
        <div class="content">{{ date }}</div>
      </div>
      <div id="info-sche">
        <div class="title">当前学期：</div>
        <div class="content">2024.01</div>
      </div>
    </div>
    <div id="hello">{{ name }}老师您好：</div>


    <router-link to="/student">
      <div id="cloud2">
        <div class="func-title" id="title2">学生<br />查询</div>
        <div class="func-content" id="content2">
          搜索获取班级学生名单以及学生积分情况，导入学生名单
        </div>
      </div>
    </router-link>

    <router-link to="/student">
      <div id="cloud3">
        <div class="func-title" id="title2">学生<br />查询</div>
        <div class="func-content" id="content2">
          搜索获取班级学生名单以及学生积分情况，导入学生名单
        </div>
      </div>
    </router-link>

    <router-link to="/roll-call">
      <div id="cloud4">
        <div class="func-title" id="title4">点名<br />提问</div>
        <div class="func-content" id="content4">对学生进行点名，并对到场的学生进行提问</div>
      </div>
    </router-link>

    <router-link to="/roll-call">
      <div id="cloud5">
        <div class="func-title" id="title5">点名<br />提问</div>
        <div class="func-content" id="content5">
          对学生进行点名，并对到场的学生进行提问
        </div>
      </div>
    </router-link>
  </div>
</template>

<script>

export default {
  data() {
    return {
      name: "FlyingPig",
      semester: "",
    };
  },
  beforeMount() {
    if (localStorage.getItem("name") == null) {
      alert("您还未登录或登录已过期，请重新登录！");
      if (this.$route.path !== "/login") {
        this.$router.push("/login");
      }
    } else {
      this.name = localStorage.getItem("name");
    }
  },
  methods: {},
  computed: {
    date: () => {
      let date = new Date();
      let year = date.getFullYear();
      let month = date.getMonth() + 1;
      let day = date.getDate();
      return `${year}年${month}月${day}日`;
    },
  },
};
</script>

<style scoped>
#home-part {
  box-sizing: border-box;
  position: relative;
  width: 85.4%;
  height: 100vh;
  background-image: url("../assets/home/backgound2.png");
  background-size: cover;
  display: flex;
  overflow: hidden; /* 隐藏溢出内容 */
}
#banner {
  box-sizing: border-box;
  position: absolute;
  margin-left: 5.5%;
  margin-top: 15px;
  width: 90%;
  height: 50px;
  background-color: #b9c1c4;
  border-radius: 20px;
  justify-content: center;
  background-size: cover;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
}
#info-sche {
  display: flex;
}
#info-time {
  display: flex;
}
.title {
  font-size: 20px;
  text-align: center;
  font-weight: bold;
}
.content {
  font-size: 20px;
  font-weight: 400;
}
#hello {
  margin-left: 50px;
  margin-top: 90px;
  font-size: 20px;
  align-self: flex-start;
  font-weight: bold;
}
@keyframes move2 {
  from {
    transform: translate(120%, -80%);
  }
  to {
    transform: translate(0%, 0%);
  }
}
@keyframes move3 {
  from {
    transform: translate(100%, 10%);
  }
  to {
    transform: translate(0%, 0%);
  }
}
@keyframes move4 {
  from {
    transform: translate(110%, 50%);
  }
  to {
    transform: translate(0%, 0%);
  }
}
@keyframes move5 {
  from {
    transform: translate(20%, 80%);
  }
  to {
    transform: translate(0%, 0%);
  }
}
#cloud2 {
  background-size: contain;
  position: absolute;
  z-index: 100;
  width: 439px;
  height: 249px;
  top: 50.9%;
  left: 0%;
  animation: move2 1.5s ease;
}
#cloud3 {
  background-size: contain;
  position: absolute;
  z-index: 100;
  width: 439px;
  height: 279px;
  top: 24.9%;
  left: 10%;
  animation: move3 1.5s ease;
}
#cloud4 {
  background-size: contain;
  position: absolute;
  z-index: 100;
  width: 333px;
  height: 319px;
  top: 10.9%;
  left: 38%;
  animation: move4 1.5s ease;
}
#cloud5 {
  background-size: contain;
  position: absolute;
  z-index: 100;
  width: 430px;
  height: 319px;
  top: 12.9%;
  left: 61%;
  animation: move5 1.5s ease;
}
.func-title {
  font-size: 22px;
  font-weight: bold;
  margin-top: 25px;
  margin-left: 20px;
  position: absolute;
  color: black;
}
.func-content {
  font-size: 18px;
  font-weight: 550;
  margin-top: 10px;
  margin-left: 20px;
  position: absolute;
}
#title2 {
  top: 23%;
  left: 60%;
  letter-spacing: 2px;
}
#content2 {
  width: 190px;
  top: 33%;
  left: 14%;
  color: #000;
}
#content2:hover {
  color: white;
}
#title3 {
  top: 38%;
  left: 60%;
  letter-spacing: 2px;
}
#title4 {
  top: 45%;
  left: 37%;
  letter-spacing: 2px;
}
#content4 {
  width: 190px;
  top: 21%;
  left: 14%;
  color: black;
}
#content4:hover {
  color: white;
}
#title5 {
  top: 35%;
  left: 15%;
  letter-spacing: 2px;
}
#content5 {
  width: 190px;
  top: 21%;
  left: 30%;
  color: #000;
}
#content5:hover {
  color: white;
}
</style>