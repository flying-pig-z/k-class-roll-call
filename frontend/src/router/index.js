import VueRouter from "vue-router";
import Login from "../pages/Login";
import Register from "../pages/Register";
import Home from "../pages/Home";
import HomePart from "../components/HomePart";
import Student from "../components/Student";
import RollCall from "../components/RollCall";
import Question from "../components/Question";
export default new VueRouter({
    mode: 'history',
    routes: [{
        path: '/login',
        component: Login
    },
    {
        path: '/register',
        component: Register
    },
    {
        path: '/',
        component: Home,
        children: [
            {
                path: 'home',
                component: HomePart
            },
            {
                path: 'student',
                component: Student
            },
            {
                path: 'roll-call',
                component: RollCall
            },
            {
                path: '/question',
                name: 'question',
                component: Question, // 这是你的组件
            }


        ],
        redirect: '/home'

    }
    ]
})