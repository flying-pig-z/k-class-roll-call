<template>
  <div id="sidebar">
    <div id="info">
      <img id="logo" src="../assets/home/avatar.png" alt="" />
      <h1 id="title">KX点名</h1>
    </div>

    <el-menu :default-active="$route.path" class="menu">
      <router-link to="/home">
        <el-menu-item index="/home" >
          <div class="menu-item">
            <img src="../assets/home/home-icon.png" alt="" />
            首页
          </div>
        </el-menu-item>
      </router-link>
      <router-link to="/student">
        <el-menu-item index="/student">
          <div class="menu-item">
            <img src="../assets/home/search-menu.png" alt="" />
            学生名单与积分查询
          </div>
        </el-menu-item>
      </router-link>
      <router-link to="/roll-call">
        <el-menu-item index="/roll-call">
          <div class="menu-item">
            <img src="../assets/home/pass-icon.png" alt="" />
            点名与提问
          </div>
        </el-menu-item>
      </router-link>
    </el-menu>
    <div id="logout" @click="logout">
      <img src="../assets/home/logout-icon.png" alt="" />
      <span>退出登录</span>
    </div>
  </div>
</template>

<script>
import { Logout } from "@/api/api";

export default {
  methods: {
    logout() {
      Logout().then(
        (res) => {
          this.$message.success("退出成功！");
          localStorage.removeItem("token");
          localStorage.removeItem("name");
          this.$router.push("/login");
        },
        (err) => {
          this.$message.error(err.msg);
          console.log(err);
        }
      );
    },
  },
};
</script>

<style scoped>
#sidebar {
  background-color: #a6e0fe;
  height: 100vh;
  width: 260px;
  min-height: 815px;
  box-shadow: 8px 0px 11px 0px rgba(0, 0, 0, 0.16);
  border-radius: 5px;
  display: flex;
  flex-direction: column;
}
#info {
  height: 95px;
  width: 260px;
  min-height: 95px;
  justify-content: center;
  align-items: center;
  position: relative;
  background: #66c7fe;
  /* box-shadow: 5px 0px 4px 0px #66c7fe; */
}
#logo {
  position: absolute;
  left: 10px;
  top: 13px;
  height: 71px;
  width: 71px;
}
#title {
  position: absolute;
  left: 105px;
  top: 26px;
  width: 135px;
  height: 30px;
  font-weight: 600;
}
.menu {
  height: 100%;
  width: 100%;
  flex-direction: column;
  justify-content: flex-start;
  position: relative;
  color: white;
  font-size: 20px;
  font-weight: 550;
}
.menu-item {
  height: 100%;
  width: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding-left: 40px;
  box-sizing: border-box;

}
a {
  text-decoration: none;
  color: white;
}
.sub-item {
  padding-left: 50px;
}
li {
  padding: 0px !important;
}

.menu-item img {
  height: 30px;
  width: 30px;
  margin-right: 10px;
}
.el-menu {
  background: transparent !important;
  border: 0 !important;
}
.el-menu-item {
  color: white !important;
  background-color: #a6e0fe !important;
  font-size: 18px !important;
}
.el-menu-item:hover {
  outline: 0;
  background-color: #6fc5f1 !important;
}
.el-menu-item.is-active {
  background-color: #6fc5f1 !important;
  padding: 0px !important;
}

#logout {
  bottom: 0;
  width: 100%;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 20px;
  font-weight: 550;
  cursor: pointer;
  border-radius: 5px;
}
#logout img {
  height: 35px;
  width: 35px;
  margin-right: 15px;
  margin-left: -30px;
}
#logout:hover {
  background: #6fc5f1;
}
</style>