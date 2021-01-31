import { Pie } from 'vue-chartjs'

export default {
  extends: Pie,
  props: ['data', 'options'],
  mounted () {
    this.renderChart(this.data, this.options);
                                
  }
  ,
  watch: {
    data(){
      this.renderChart(this.data, this.options);
    }
  },
}
