import { createRouter, createWebHistory } from 'vue-router'
import Login from "../views/login/LoginViews.vue"
import HomeView from '../views/index.vue'
import Home from '../views/home/index.vue'
import WorkPlace from '../views/workPlace/index.vue'
import WorkPlaceMain from '../views/workPlace/main/index.vue'
import User from '../views/workPlace/user/index.vue'
import Quest from "../views/workPlace/quest/index.vue"
import Equipment from "../views/workPlace/equipment/index.vue"
import Facility from "../views/workPlace/facility/index.vue"
import Team from '../views/workPlace/team/index.vue'
import Abnormality from "../views/workPlace/abnormality/index.vue"
import EmailMain from "../views/email/index.vue"
import EmailInbox from "../views/email/components/inbox.vue"
import EmailSent from "../views/email/components/sent.vue"
import EmailDrafts from "../views/email/components/drafts.vue"
import NotFound from "../views/notfound/index.vue"


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: HomeView,
      children:[
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
            },
            {
              path: 'abnormality',
              component: Abnormality,
            },
            {
              path: 'quest',
              component: Quest,
            },
            {
              path: 'facility',
              component: Facility,
            },
            {
              path: 'equipment',
              component: Equipment,
            }

          ]
        },
        {
          path: 'start',
          name: '/start',
          component: Home,
        },
        {
          path: '',
          name: '',
          component: Home,
        },
        {
          path: 'email',
          name: '/email',
          component: EmailMain,
          children:[
            {
              path: 'inbox',
              component:  EmailInbox,
            },
            {
              path: 'drafts',
              component:  EmailDrafts,
            },
            {
              path: 'sent',
              component: EmailSent,
            },
          ]
        }
      ]
    },
    {
      path: '',
      component: Login,

    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component:NotFound,
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
