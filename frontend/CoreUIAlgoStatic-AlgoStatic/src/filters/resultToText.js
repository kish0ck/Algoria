export default (result = "") => {
  switch (result) {
    case "result-ac":
      return "맞았습니다";
    case "result-wa":
      return "틀렸습니다";
    case "result-tle":
      return "시간초과";
    case "result-rte":
      return "런타임에러";
    case "result-ce":
      return "컴파일에러";
    case "result-ole":
      return "출력초과";
    case "result-pe":
      return "출력형식";
    case "result-mle":
      return "메모리초과";
    default:
      return result;
  }
};
