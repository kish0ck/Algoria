export default [
  {
    _name: 'CSidebarNav',
    _children: [
      {
        _name: 'CSidebarNavItem',
        name: 'Dashboard',
        to: 'dashboard',
        icon: 'cil-speedometer',
        badge: {
          color: 'primary',
          text: ''
        }
      },
      {
        _name: 'CSidebarNavItem',
        name: 'Feedback',
        to: 'feedback',
        icon: 'cil-puzzle',
      },
      {
        _name: 'CSidebarNavItem',
        name: 'Recommend',
        to: 'recommend',
        icon: 'cil-star',
      },
    ]
  }
]