import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { 
  Bars3Icon, 
  XCircleIcon,
  HomeIcon,
  PlayIcon,
  TruckIcon,
  MapIcon,
  ShoppingBagIcon,
  UserCircleIcon,
  ArrowRightOnRectangleIcon
} from '@heroicons/react/24/outline';
import { Link, useLocation } from 'react-router-dom';

const Layout = ({ children }) => {
  const { user, logout } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();

  const navigation = [
    { name: 'Dashboard', href: '/dashboard', icon: HomeIcon },
    { name: 'Simulation', href: '/simulation', icon: PlayIcon },
    { name: 'Drivers', href: '/drivers', icon: TruckIcon },
    { name: 'Routes', href: '/routes', icon: MapIcon },
    { name: 'Orders', href: '/orders', icon: ShoppingBagIcon },
  ];

  const isActiveRoute = (href) => {
    return location.pathname === href;
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Mobile sidebar overlay */}
      {sidebarOpen && (
        <div 
          className="fixed inset-0 z-40 bg-gray-600 bg-opacity-75 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <div className={`fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform ${
        sidebarOpen ? 'translate-x-0' : '-translate-x-full'
      } transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:inset-0`}>
        
        {/* Sidebar header */}
        <div className="flex items-center justify-between h-16 px-6 bg-primary-600">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <TruckIcon className="w-8 h-8 text-white" />
            </div>
            <div className="ml-3">
              <h1 className="text-lg font-semibold text-white">GreenCart</h1>
              <p className="text-xs text-primary-100">Logistics</p>
            </div>
          </div>
          <button
            className="lg:hidden text-white hover:text-primary-200"
            onClick={() => setSidebarOpen(false)}
          >
            <XCircleIcon className="w-6 h-6" />
          </button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 px-4 py-6 space-y-2">
          {navigation.map((item) => {
            const Icon = item.icon;
            const active = isActiveRoute(item.href);
            
            return (
              <Link
                key={item.name}
                to={item.href}
                className={`flex items-center px-3 py-2 text-sm font-medium rounded-lg transition-colors ${
                  active
                    ? 'bg-primary-50 text-primary-700 border-r-2 border-primary-600'
                    : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                }`}
                onClick={() => setSidebarOpen(false)}
              >
                <Icon className={`w-5 h-5 mr-3 ${active ? 'text-primary-600' : 'text-gray-400'}`} />
                {item.name}
              </Link>
            );
          })}
        </nav>

        {/* User info */}
        <div className="px-4 py-4 border-t border-gray-200">
          <div className="flex items-center">
            <UserCircleIcon className="w-8 h-8 text-gray-400" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-900">{user?.username}</p>
              <p className="text-xs text-gray-500">{user?.roles?.join(', ')}</p>
            </div>
          </div>
          <button
            onClick={logout}
            className="flex items-center w-full mt-3 px-3 py-2 text-sm font-medium text-red-600 hover:bg-red-50 rounded-lg transition-colors"
          >
            <ArrowRightOnRectangleIcon className="w-5 h-5 mr-3" />
            Logout
          </button>
        </div>
      </div>

      {/* Main content */}
      <div className="lg:pl-64">
        {/* Top bar */}
        <div className="sticky top-0 z-30 bg-white border-b border-gray-200 lg:hidden">
          <div className="flex items-center justify-between h-16 px-4">
            <button
              className="text-gray-500 hover:text-gray-900"
              onClick={() => setSidebarOpen(true)}
            >
              <Bars3Icon className="w-6 h-6" />
            </button>
            <div className="flex items-center">
              <TruckIcon className="w-6 h-6 text-primary-600 mr-2" />
              <span className="text-lg font-semibold text-gray-900">GreenCart</span>
            </div>
          </div>
        </div>

        {/* Page content */}
        <main className="flex-1">
          <div className="p-6">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
};

export default Layout;
