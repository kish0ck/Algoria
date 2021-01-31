const tiers = {
  0: "Bronze",
  1: "Silver",
  2: "Gold",
  3: "Platinum",
  4: "Diamond",
  5: "Ruby"
};
const levels = {
  0: "I",
  4: "II",
  3: "III",
  2: "IV",
  1: "V"
};
export default (value = "") => {
  value = Number(value);
  if (value != 0) {
    let tier = parseInt((value - 1) / 5);
    let level = value % 5;
    return tiers[tier]+ " "+levels[level];
  } else {
    return "Unranked";
  }
};
