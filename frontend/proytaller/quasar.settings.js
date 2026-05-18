// quasar.settings.js
import { Notify, Dialog, Loading } from 'quasar'
import iconSetMaterial from 'quasar/icon-set/material-icons'

export default {
  plugins: { Notify, Dialog, Loading },
  config: {
    brand: {
      primary: '#027be3',
      secondary: '#26A69A',
      accent: '#9C27B0',
      positive: '#21BA45',
      negative: '#C10015',
      info: '#31CCEC',
      warning: '#F2C037'
    },
    notify: {}
  },
  iconSet: {
    ...iconSetMaterial,
    field: {
      error: 'report_problem',
      success: 'check_circle',
      warning: 'warning'
    }
  }
}
