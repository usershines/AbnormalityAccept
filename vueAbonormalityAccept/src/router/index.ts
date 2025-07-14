import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/index.vue'
import EmailView from '../views/email/index.vue'
import Home from '../views/home/index.vue'
import WorkPlace from '../views/workPlace/index.vue'
import User from '../views/workPlace/user/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: HomeView,
      children:[
        {
          path: 'email',
          name: 'email',
          component: EmailView,
        },
        {
          path: 'workPlace',
          name: 'workPlace',
          component: WorkPlace,
          children:[
            {
              path: 'user',
              component: User,
            }
          ]
        },
        {
          path: 'start',
          name: 'start',
          component: Home,
        },
        {
          path: '',
          name: '',
          component: Home,
        },
      ]
    },

    /*{
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },*/
  ],
})

export default router
