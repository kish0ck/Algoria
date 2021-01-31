<template>
  <div id="search">
    <v-card color="blue">
      <div class="page">
        <div class="page__demo">
          <div style="text-align:center;">
            <img src="img/logo_transparent.png" style="max-width:100%; height:auto; width:50%;" />
          </div>
          <div
            style="color:white; min-height:5vh; display:flex; align-items:center; justify-content:center;"
          >
            <vue-typed-js :strings="['Statistics', 'Feedback', 'Recommendation']" :loop="true">
              <h2>
                Algoria is
                <span class="typing"></span>
                Service!
              </h2>
            </vue-typed-js>
          </div>
          <br />

          <form class="search">
            <div class="a-field search__field">
              <input
                type="text"
                id="query"
                v-model="searchQuery"
                class="r-text-field a-field__input search__input"
                placeholder="백준 아이디를 입력해주세요."
                autocomplete="off"
                required
              />
              <button class="r-button search__button search__clear" type="reset">
                <span class="search__clear-label">Clear the search form</span>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 47.971 47.971"
                  class="search__icon search__icon-clear"
                >
                  <path
                    d="M28.228 23.986L47.092 5.122a2.998 2.998 0 0 0 0-4.242 2.998 2.998 0 0 0-4.242 0L23.986 19.744 5.121.88a2.998 2.998 0 0 0-4.242 0 2.998 2.998 0 0 0 0 4.242l18.865 18.864L.879 42.85a2.998 2.998 0 1 0 4.242 4.241l18.865-18.864L42.85 47.091c.586.586 1.354.879 2.121.879s1.535-.293 2.121-.879a2.998 2.998 0 0 0 0-4.242L28.228 23.986z"
                  />
                </svg>
              </button>
              <label class="a-field__label-wrap search__hint" for="query">
                <span class="a-field__label">Search User</span>
              </label>
            </div>
            <button class="r-button search__button search__submit" @click.stop.prevent="submit()">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 56.966 56.966"
                class="search__icon search__icon-search"
              >
                <path
                  d="M55.146 51.887L41.588 37.786A22.926 22.926 0 0 0 46.984 23c0-12.682-10.318-23-23-23s-23 10.318-23 23 10.318 23 23 23c4.761 0 9.298-1.436 13.177-4.162l13.661 14.208c.571.593 1.339.92 2.162.92.779 0 1.518-.297 2.079-.837a3.004 3.004 0 0 0 .083-4.242zM23.984 6c9.374 0 17 7.626 17 17s-7.626 17-17 17-17-7.626-17-17 7.626-17 17-17z"
                />
              </svg>
            </button>
          </form>
        </div>
      </div>
    </v-card>
    <v-if="isShow">
      <notification :msg='msg' />
    </v-if>
  </div>
</template>

<script>
import notification from "@/components/Notification";
export default {
  props: {
    msg: {
      type: String,
      default: null
    }
  },
  components: {
    notification
  },
  watch:{
    msg : function(newMessage){
      this.show = false;
      this.show = this.msg!=null;
    }
  },
  data() {
    return {
      show : false,
      snackbar : false,
      searchQuery: ""
    };
  },
  methods: {
    submit() {
      //대문자 -> 소문자
      this.$router.push("/" + this.searchQuery.toLowerCase());
    }
  }
};
</script>

<style scoped>
.slide-enter-active {
  -moz-transition-duration: 0.5s;
  -webkit-transition-duration: 0.5s;
  -o-transition-duration: 0.5s;
  transition-duration: 0.5s;
  -moz-transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  -webkit-transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  -o-transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-leave-active {
  -moz-transition-duration: 0.5s;
  -webkit-transition-duration: 0.5s;
  -o-transition-duration: 0.5s;
  transition-duration: 0.5s;
  -moz-transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  -webkit-transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  -o-transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-enter-to,
.slide-leave {
  max-height: 100px;
  overflow: hidden;
}

.slide-enter,
.slide-leave-to {
  overflow: hidden;
  max-height: 0;
}

@media (min-width: 768px) {
  html {
    font-size: 62.5%;
  }
}

@media (max-width: 767px) {
  html {
    font-size: 50%;
  }
}

body {
  font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Open Sans,
    Ubuntu, Fira Sans, Helvetica Neue, sans-serif;
  font-size: 1.6rem;
  margin: 0;
}

.page {
  min-height: 100vh;
  display: flex;
}

.page__demo {
  margin: auto;
}

.page__demo {
  box-sizing: border-box;
  width: 100%;
  max-width: 640px;
  padding: 20px;
}

.page__hint {
  display: block;
  max-width: 26rem;
  line-height: 1.678;

  margin-left: auto;
  margin-right: auto;
  margin-top: 3rem;

  color: #fff;
  text-align: center;
}
/*
* The r-text-field component
*/

/*
The component will reset input browser styles
*/

.r-text-field {
  --uiTextFieldPlaceholderColor: var(--textFieldPlaceholderColor);

  font-family: inherit;
  font-size: inherit;
  -webkit-appearance: none;
  -moz-appearance: none;

  border-radius: 0;
  border-style: solid;
}

.r-text-field::-webkit-input-placeholder {
  color: var(--uiTextFieldPlaceholderColor);
}

.r-text-field::-moz-placeholder {
  opacity: 1;
  color: var(--uiTextFieldPlaceholderColor);
}

.r-text-field:invalid {
  outline: none;
  box-shadow: none;
}

/*
* The r-button component
*/

/*
The component will reset button browser styles
*/

.r-button {
  background-color: transparent;
  padding: 0;
  border: none;
  cursor: pointer;

  font-family: inherit;
  font-size: inherit;
}

/* Remove the inner border and padding in Firefox. */

.r-button::-moz-focus-inner,
.r-button[type="button"]::-moz-focus-inner,
.r-button[type="reset"]::-moz-focus-inner,
.r-button[type="submit"]::-moz-focus-inner {
  border-style: none;
  padding: 0;
}

/*
* The a-field component
*/

/*
 1. The --aFieldHeight option defines an input height. And this option is needed for calculations of a hint final position
 2. The --aFieldHintFinalCoordinate option defines calculations of the final Y-axis coordinate
 3. The —aFieldPaddingLeft option defines left indent for a hint and query text
*/

.a-field {
  --uiaFieldHeight: var(--aFieldHeight);
  --uiaFieldHintFinalCoordinate: var(
    --aFieldHintFinalCoordinate,
    var(--uiaFieldHeight)
  );
  --uiaFieldPaddingLeft: var(--aFieldPaddingLeft);

  position: relative;
  padding-top: calc(
    var(--uiaFieldHintFinalCoordinate) - var(--uiaFieldHeight)
  ); /* 1, 2 */
}

.a-field__input {
  box-sizing: border-box;
  height: var(--uiaFieldHeight); /* 1 */
  padding-top: 0;
  padding-bottom: 0;
  padding-left: var(--uiaFieldPaddingLeft); /* 3 */
}

.a-field__input::-webkit-input-placeholder {
  opacity: 0;
  transition: opacity 0.2s ease-out;
}

.a-field__input::-moz-placeholder {
  opacity: 0;
}

.a-field__input:focus::-webkit-input-placeholder {
  opacity: 1;
  transition-delay: 0.2s;
}

.a-field__input:focus::-moz-placeholder {
  opacity: 1;
}

.a-field__label-wrap {
  box-sizing: border-box;
  width: 100%;
  height: var(--uiaFieldHeight); /* 1 */

  pointer-events: none;
  cursor: text;

  position: absolute;
  bottom: 0;
  left: 0;
}

.a-field__label {
  position: absolute;
  left: var(--uiaFieldPaddingLeft); /* 3 */
  bottom: calc(50% - 0.5em);
  line-height: 1;
  pointer-events: none;
}

/*
  I needed to create an animation onle Chromium broswers because:
  1. Firefox doesn't support a placeholder animation
  2. I couldn't make an correct animation at Edge
*/

@supports (-webkit-appearance: none) {
  .a-field__label {
    transition: bottom 0.2s cubic-bezier(0.9, -0.15, 0.1, 1.15),
      opacity 0.2s ease-out;
    will-change: bottom, opacity;
  }
}

/* If a user had inserted a text of query a hint will be hidden */

.a-field__input:not(:placeholder-shown) ~ .a-field__label-wrap .a-field__label {
  opacity: 0;
  bottom: 85%;
}

/* If the focal point is a field a hint will be displayed */

.a-field__input:focus ~ .a-field__label-wrap .a-field__label {
  opacity: 1;
  bottom: var(--uiaFieldHintFinalCoordinate); /* 2 */
}

/*
* the search component
*/

.search {
  --searchFieldHeight: 50px;
  --searchButtonSize: var(--searchFieldHeight);
  --textFieldPlaceholderColor: #fff;

  --aFieldHeight: var(--searchFieldHeight);
  /* The 10px value has been calculated for this demo and you can change it */
  --aFieldHintFinalCoordinate: calc(var(--searchFieldHeight) + 10px);
  --aFieldPaddingLeft: 30px;

  color: #fff;
  position: relative;
}

/*
 1. I needed to set the currentColor value for correct displaying a border on iOS devices
*/

.search__input {
  width: 100%;
  border-width: 3px;
  border-color: currentColor; /* 1 */

  border-radius: 25px;
  background-color: transparent;

  font-size: 18px;
  color: currentColor;
}

.search__input:focus {
  outline: none;
}

.search__hint {
  font-size: 16px;
}

.search__button {
  color: currentColor;
}

.search__button:focus {
  outline: none;
}

.search__clear {
  width: 1em;
  height: 1em;
  font-size: var(--searchButtonSize);

  position: absolute;
  bottom: 0;
  display: none;
}

/* If a user starts typing a clearing button will be displayed */

.search__input:not(:placeholder-shown) ~ .search__clear {
  display: block;
}

/* If the focal point is a clearing button the hint will be displayed */

.search__clear:focus ~ .a-field__label-wrap .a-field__label {
  opacity: 1;
  bottom: var(--aFieldHintFinalCoordinate);
}

.search__clear-label {
  position: absolute;
  clip: rect(0 0 0 0);
  overflow: hidden;
  width: 1px;
  height: 1px;
  margin: -1px;
}

.search__icon {
  fill: currentColor;
  display: block;

  box-sizing: border-box;
  width: 1.5em;
  height: 1.5em;
  padding: 0.25em;

  position: absolute;
  top: calc(50% - 0.75em);
  left: calc(50% - 0.75em);
}

.search__button:focus .search__icon {
  box-shadow: 0 0 0 3px currentColor;
}

.search__icon-clear {
  font-size: 18px;
}

@media (max-width: 640px) {
  .search__input {
    padding-right: 50px;
  }

  .search__clear {
    right: 10px;
  }

  .search__submit:focus {
    box-shadow: 0 0 0 3px currentColor;
    color: #422380;
  }

  .search__icon-search {
    display: none;
  }
}

@media (min-width: 641px) {
  .search__input {
    padding-right: 115px;
  }

  .search__clear {
    right: var(--searchButtonSize);
  }

  .search__submit {
    width: 1em;
    height: 1em;
    font-size: var(--searchButtonSize);

    position: absolute;
    bottom: 0;
    right: 10px;
  }

  .search__submit-label {
    position: absolute;
    clip: rect(0 0 0 0);
    overflow: hidden;
    width: 1px;
    height: 1px;
    margin: -1px;
  }

  .search__icon-search {
    font-size: 22px;
  }
}
</style>
