import axios from 'axios'


axios.defaults.baseURL = "http://localhost:8091"

// 前置拦截
axios.interceptors.request.use(config => {
  return config
})

axios.interceptors.response.use(response => {
    let res = response.data;

    console.log("=================")
    console.log(res)
    console.log("=================")

    if (response.status === 200) {
      return response
    } else {
      return Promise.reject(response.code)
    }
  },
  error => {
    console.log(error)
    if(error.response.data) {
      error.message = error.response.data.msg
    }
    return Promise.reject(error)
  }
)