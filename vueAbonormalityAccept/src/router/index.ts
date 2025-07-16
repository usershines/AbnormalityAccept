import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/index.vue'
import EmailView from '../views/email/index.vue'
import Home from '../views/home/index.vue'
import WorkPlace from '../views/workPlace/index.vue'
import User from '../views/workPlace/user/index.vue'
import WorkPlaceMain from '../views/workPlace/main/index.vue'
import Team from '../views/workPlace/team/index.vue'
import Login from "../views/login/LoginViews.vue";

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
              path: '',
              component: WorkPlaceMain,
            },
            {
              path: 'user',
              name: 'user',
              component: User,
            },
            {
              path: 'main',
              component: WorkPlaceMain,
            },
            {
              path: 'team',
              component: Team,
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
    {
      path: '/login',
      component: Login,

    }
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
