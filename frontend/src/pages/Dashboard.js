import React, { useState, useEffect } from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';
import { 
  CurrencyRupeeIcon, 
  ChartBarIcon, 
  TruckIcon, 
  ClockIcon,
  ExclamationTriangleIcon,
  CheckCircleIcon
} from '@heroicons/react/24/outline';
import { 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, LineChart, Line, Legend
} from 'recharts';
import LoadingSpinner from '../components/LoadingSpinner';

const Dashboard = () => {
  const [loading, setLoading] = useState(true);
  const [dashboardData, setDashboardData] = useState({
    totalProfit: 0,
    efficiencyScore: 0,
    onTimeDeliveries: 0,
    lateDeliveries: 0,
    totalDeliveries: 0,
    fuelCostBreakdown: {},
    activeDrivers: 0,
    activeRoutes: 0,
    pendingOrders: 0
  });
  const [simulationHistory, setSimulationHistory] = useState([]);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      // Fetch latest simulation data
      const [
        simulationResponse,
        driversResponse,
        routesResponse,
        ordersResponse,
        historyResponse
      ] = await Promise.all([
        axios.get('/simulation/latest').catch(() => ({ data: null })),
        axios.get('/drivers/count').catch(() => ({ data: 0 })),
        axios.get('/routes/active').catch(() => ({ data: [] })),
        axios.get('/orders/status/PENDING').catch(() => ({ data: [] })),
        axios.get('/simulation/history').catch(() => ({ data: [] }))
      ]);

      const latestSimulation = simulationResponse.data;
      
      setDashboardData({
        totalProfit: latestSimulation?.totalProfit || 0,
        efficiencyScore: latestSimulation?.efficiencyScore || 0,
        onTimeDeliveries: latestSimulation?.onTimeDeliveries || 0,
        lateDeliveries: latestSimulation?.lateDeliveries || 0,
        totalDeliveries: latestSimulation?.totalDeliveries || 0,
        fuelCostBreakdown: latestSimulation?.fuelCostBreakdown || {},
        activeDrivers: driversResponse.data,
        activeRoutes: routesResponse.data.length,
        pendingOrders: ordersResponse.data.length
      });

      setSimulationHistory(historyResponse.data.slice(0, 5)); // Last 5 simulations

    } catch (error) {
      console.error('Error fetching dashboard data:', error);
      toast.error('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner text="Loading dashboard..." />;
  }

  // Chart data preparation
  const deliveryData = [
    { name: 'On Time', value: dashboardData.onTimeDeliveries, color: '#22c55e' },
    { name: 'Late', value: dashboardData.lateDeliveries, color: '#ef4444' }
  ];

  const fuelCostData = Object.entries(dashboardData.fuelCostBreakdown).map(([level, cost]) => ({
    name: level,
    cost: cost,
    color: level === 'High' ? '#ef4444' : level === 'Medium' ? '#f59e0b' : '#22c55e'
  }));

  const historyData = simulationHistory.map((sim, index) => ({
    name: `Sim ${simulationHistory.length - index}`,
    profit: sim.totalProfit,
    efficiency: sim.efficiencyScore
  }));

  // KPI Cards
  const kpiCards = [
    {
      title: 'Total Profit',
      value: `₹${dashboardData.totalProfit.toLocaleString()}`,
      icon: CurrencyRupeeIcon,
      color: 'text-green-600',
      bgColor: 'bg-green-50',
      change: '+12.5%',
      changeColor: 'text-green-600'
    },
    {
      title: 'Efficiency Score',
      value: `${dashboardData.efficiencyScore.toFixed(1)}%`,
      icon: ChartBarIcon,
      color: 'text-blue-600',
      bgColor: 'bg-blue-50',
      change: '+5.2%',
      changeColor: 'text-green-600'
    },
    {
      title: 'Active Drivers',
      value: dashboardData.activeDrivers,
      icon: TruckIcon,
      color: 'text-purple-600',
      bgColor: 'bg-purple-50',
      change: 'No change',
      changeColor: 'text-gray-500'
    },
    {
      title: 'Pending Orders',
      value: dashboardData.pendingOrders,
      icon: ClockIcon,
      color: 'text-orange-600',
      bgColor: 'bg-orange-50',
      change: '-3 orders',
      changeColor: 'text-green-600'
    }
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
            <p className="text-gray-600">Overview of delivery operations and KPIs</p>
          </div>
          <button
            onClick={fetchDashboardData}
            className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
          >
            Refresh Data
          </button>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {kpiCards.map((card, index) => {
          const Icon = card.icon;
          return (
            <div key={index} className="bg-white rounded-lg shadow-sm border p-6 hover:shadow-md transition-shadow">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{card.title}</p>
                  <p className="text-2xl font-bold text-gray-900 mt-1">{card.value}</p>
                  <p className={`text-xs mt-1 ${card.changeColor}`}>{card.change}</p>
                </div>
                <div className={`p-3 rounded-lg ${card.bgColor}`}>
                  <Icon className={`w-6 h-6 ${card.color}`} />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Delivery Performance Chart */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Delivery Performance</h3>
          {dashboardData.totalDeliveries > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={deliveryData}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={100}
                  paddingAngle={5}
                  dataKey="value"
                >
                  {deliveryData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => [`${value} deliveries`, '']} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="flex items-center justify-center h-[300px] text-gray-500">
              <div className="text-center">
                <ExclamationTriangleIcon className="w-12 h-12 mx-auto mb-2 text-gray-400" />
                <p>No delivery data available</p>
                <p className="text-sm">Run a simulation to see results</p>
              </div>
            </div>
          )}
        </div>

        {/* Fuel Cost Breakdown */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Fuel Cost by Traffic Level</h3>
          {fuelCostData.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={fuelCostData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip formatter={(value) => [`₹${value.toFixed(2)}`, 'Fuel Cost']} />
                <Bar dataKey="cost" fill="#3b82f6" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <div className="flex items-center justify-center h-[300px] text-gray-500">
              <div className="text-center">
                <ChartBarIcon className="w-12 h-12 mx-auto mb-2 text-gray-400" />
                <p>No fuel cost data available</p>
                <p className="text-sm">Run a simulation to see breakdown</p>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Simulation History */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Simulation History</h3>
        {historyData.length > 0 ? (
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={historyData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis yAxisId="profit" orientation="left" />
              <YAxis yAxisId="efficiency" orientation="right" />
              <Tooltip />
              <Legend />
              <Bar yAxisId="profit" dataKey="profit" fill="#22c55e" name="Profit (₹)" />
              <Line yAxisId="efficiency" type="monotone" dataKey="efficiency" stroke="#3b82f6" name="Efficiency (%)" />
            </LineChart>
          </ResponsiveContainer>
        ) : (
          <div className="flex items-center justify-center h-[300px] text-gray-500">
            <div className="text-center">
              <ClockIcon className="w-12 h-12 mx-auto mb-2 text-gray-400" />
              <p>No simulation history available</p>
              <p className="text-sm">Run some simulations to see trends</p>
            </div>
          </div>
        )}
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center">
            <CheckCircleIcon className="w-8 h-8 text-green-600 mr-3" />
            <div>
              <p className="text-sm text-gray-600">On-Time Deliveries</p>
              <p className="text-xl font-bold text-gray-900">{dashboardData.onTimeDeliveries}</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center">
            <ExclamationTriangleIcon className="w-8 h-8 text-red-600 mr-3" />
            <div>
              <p className="text-sm text-gray-600">Late Deliveries</p>
              <p className="text-xl font-bold text-gray-900">{dashboardData.lateDeliveries}</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center">
            <TruckIcon className="w-8 h-8 text-blue-600 mr-3" />
            <div>
              <p className="text-sm text-gray-600">Active Routes</p>
              <p className="text-xl font-bold text-gray-900">{dashboardData.activeRoutes}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
