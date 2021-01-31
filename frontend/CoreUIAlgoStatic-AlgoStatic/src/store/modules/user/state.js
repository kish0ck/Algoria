export default {
  //+user
  /***
   * user_id
   * problems
   * sucess/fail/total
   * problems_Type
   * ***/
  user_id: "undefined",
  problems: null,
  problems_type: null,
  user_info: {
    type: Object,
    default: {
      user_id: {
        type: String,
        default: "undefined"
      },
      solved: {
        type: Number,
        default: -1
      },
      exp: {
        type: Number,
        default: -1
      },
      level: {
        type: Number,
        default: -1
      },
      class: {
        type: Number,
        default: -1
      },
      class_decoration: {
        type: Number,
        default: -1
      },
      vote_count: {
        type: Number,
        default: -1
      },
      boj_submission_count: {
        type: Number,
        default: -1
      },
      boj_solved_count: {
        type: Number,
        default: -1
      },
      updated_at: {
        type: Number,
        default: -1
      }
    }
  },
  problems_data: {
    type: Object,
    default: null,
    success: {
      default: 0,
      type: Number
    },
    fail: {
      default: 0,
      type: Number
    },
    total: {
      default: 0,
      type: Number
    },
    submit_arr: {
      type: Array,
      default: function() {
        //Object로 리턴됨
        // return []
        return () => []
      }
    }
  },
  recent_submit: {
    type: Array,
    default: function() {
      // return []
      return () => []
    }
  }
};
