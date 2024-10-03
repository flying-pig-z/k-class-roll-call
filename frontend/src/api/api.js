import {
    service
} from "@/utils/request";

export function Semester() {
    return service.request({
        method: "get",
        url: `/courseDetails/dataColumn`,
    })
}

export function Login(data) {
    return service.request({
        method: "post",
        url: `/user/login`,
        data
    })
}
export function Register(data) {
    return service.request({
        method: "post",
        url: `/user/register`,
        data
    })
}

export function Logout() {
    return service.request({
        method: "post",
        url: `/user/logout`,
    })
}

export function DeleteStudent(studentId) {
    return service.request({
        method: "delete",
        url: `/student/${studentId}` // 确保 studentId 是字符串
    });
}


export function ModifyStudentScore(id, score) {
    return service.request({
        method: "put",
        url: `/student/score?id=${id}&&score=${score}`,
    })
}



export function StudentSearch(data) {
    const {
        no,
        name,
        pageSize,
        pageNo
    } = data;

    // 使用条件运算符来确保查询参数不是 undefined
    const queryParams = [
        `no=${no}`,
        `name=${name}`,
        `pageSize=${pageSize}`,
        `pageNo=${pageNo}`
    ].filter(param => param !== undefined).join('&');

    const url = `/student/search?${queryParams}`;

    return service.request({
        method: "get",
        url: url,
    });
}
export function GetRollBackStudent(mode) {
    const url = `/student/roll-call?mode=${mode}`;

    return service.request({
        method: "get",
        url: url
    });
}
