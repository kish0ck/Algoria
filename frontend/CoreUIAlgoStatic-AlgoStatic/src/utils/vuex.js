// setter
export const set = property => (state, payload) => {
  // console.warn("SET >> " + state[property]);
  return (state[property] = payload);
};

// getter
export const get = property => state => {
  // console.warn("GET >> " + state[property])
  return state[property];
};

// drawer
export const toggle = property => state => {
  // console.warn("TOGGLE >> " + state[property])
  return state[property] = !state[property]
}

// 삭제
export const blank = property => state => (state[property] = null);
