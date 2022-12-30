// global variables
const globalObj = {}

globalObj.URL =

globalObj.getAge = function (birthYear) {
  return new Date().getFullYear() - birthYear
}

export default globalObj
