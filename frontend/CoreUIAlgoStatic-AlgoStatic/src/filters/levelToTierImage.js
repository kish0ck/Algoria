export default (value = "0", opt = "tier-small") => {
  let src = "https://solved.ac/res/";
  //tier-large | tier-small
  let option = opt;
  let tier = value;
  let ext = ".svg";
  return src.concat(opt,"/",tier, ext);
};
