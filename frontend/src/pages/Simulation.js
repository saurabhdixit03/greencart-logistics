import React, { useState, useEffect } from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';
import { useForm } from 'react-hook-form';
import { 
  PlayIcon, 
  ClockIcon, 
  TruckIcon,
  CurrencyRupeeIcon,
  ChartBarIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  InformationCircleIcon
} from '@heroicons/react/outline';
import LoadingSpinner from '../components/LoadingSpinner';

const Simulation = () => {
  const [loading, setLoading] = useState(false);
  const [simulationResult, setSimulationResult] = useState(null);
  const [simulationHistory, setSimulationHistory] = useState([]);
  const [availableDrivers, setAvailableDrivers] = useState(0);
  
  const { register, handleSubmit, formState: { errors }, reset } = useForm({
    defaultValues: {
      numberOfDrivers: 3,
      routeStartTime: '09:00',
      maxHoursPerDriver: 8,
      notes: ''
    }
  });

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    try {
      const [driversResponse, historyResponse] = await Promise.all([
        axios.get('/drivers/count'),
        axios.get('/simulation/history/my')
      ]);

      setAvailableDrivers(driversResponse.data);
      setSimulationHistory(historyResponse.data);
    } catch (error) {
      console.error('Error fetching initial data:', error);
      toast.error('Failed to load simulation data');
    }
  };

  const onSubmit = async (data) => {
    try {
      setLoading(true);
      
      const response = await axios.post('/simulation/run', {
        numberOfDrivers: parseInt(data.numberOfDrivers),
        routeStartTime: data.routeStartTime,
        maxHoursPerDriver: parseInt(data.maxHoursPerDriver),
        notes: data.notes
      });

      setSimulationResult(response.data);
      toast.success('Simulation completed successfully!');
      
      // Refresh history
      fetchInitialData();
      
    } catch (error) {
      console.error('Simulation error:', error);
      const message = error.response?.data?.message || 'Simulation failed';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  const formatDateTime = (dateTimeString) => {
    return new Date(dateTimeString).toLocaleString();
  };

  const formatCurrency = (amount) => {
    return `₹${amount.toLocaleString()}`;
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Delivery Simulation</h1>
            <p className="text-gray-600">Run simulations to optimize delivery operations and calculate KPIs</p>
          </div>
          <div className="text-right">
            <p className="text-sm text-gray-600">Available Drivers</p>
            <p className="text-2xl font-bold text-primary-600">{availableDrivers}</p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Simulation Form */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-lg shadow-sm border p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Simulation Parameters</h2>
            
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Number of Drivers
                </label>
                <input
                  type="number"
                  min="1"
                  max={availableDrivers}
                  {...register('numberOfDrivers', { 
                    required: 'Number of drivers is required',
                    min: { value: 1, message: 'At least 1 driver required' },
                    max: { value: availableDrivers, message: `Maximum ${availableDrivers} drivers available` }
                  })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-primary-500 focus:border-primary-500"
                />
                {errors.numberOfDrivers && (
                  <p className="text-red-500 text-xs mt-1">{errors.numberOfDrivers.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Route Start Time
                </label>
                <input
                  type="time"
                  {...register('routeStartTime', { required: 'Start time is required' })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-primary-500 focus:border-primary-500"
                />
                {errors.routeStartTime && (
                  <p className="text-red-500 text-xs mt-1">{errors.routeStartTime.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Max Hours per Driver
                </label>
                <input
                  type="number"
                  min="1"
                  max="24"
                  {...register('maxHoursPerDriver', { 
                    required: 'Max hours is required',
                    min: { value: 1, message: 'At least 1 hour required' },
                    max: { value: 24, message: 'Maximum 24 hours allowed' }
                  })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-primary-500 focus:border-primary-500"
                />
                {errors.maxHoursPerDriver && (
                  <p className="text-red-500 text-xs mt-1">{errors.maxHoursPerDriver.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Notes (Optional)
                </label>
                <textarea
                  rows="3"
                  {...register('notes')}
                  placeholder="Add any notes about this simulation..."
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-primary-500 focus:border-primary-500"
                />
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full flex items-center justify-center py-3 px-4 bg-primary-600 text-white rounded-lg hover:bg-primary-700 focus:ring-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {loading ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full spinner mr-2"></div>
                    Running Simulation...
                  </>
                ) : (
                  <>
                    <PlayIcon className="w-5 h-5 mr-2" />
                    Run Simulation
                  </>
                )}
              </button>
            </form>

            {/* Company Rules Info */}
            <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
              <div className="flex items-start">
                <InformationCircleIcon className="w-5 h-5 text-blue-600 mt-0.5 mr-2 flex-shrink-0" />
                <div>
                  <h4 className="text-sm font-medium text-blue-800 mb-2">Company Rules Applied:</h4>
                  <ul className="text-xs text-blue-700 space-y-1">
                    <li>• Late penalty: ₹50 if delivery > base time + 10 min</li>
                    <li>• Fatigue penalty: 30% slower if driver worked >8h yesterday</li>
                    <li>• High-value bonus: 10% if order >₹1000 AND on-time</li>
                    <li>• Fuel cost: ₹5/km + ₹2/km surcharge for high traffic</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Simulation Results */}
        <div className="lg:col-span-2">
          {simulationResult ? (
            <div className="space-y-6">
              {/* Results Header */}
              <div className="bg-white rounded-lg shadow-sm border p-6">
                <div className="flex items-center justify-between mb-4">
                  <h2 className="text-lg font-semibold text-gray-900">Simulation Results</h2>
                  <span className="text-sm text-gray-500">
                    {formatDateTime(simulationResult.simulationTimestamp)}
                  </span>
                </div>

                {/* Key Metrics */}
                <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
                  <div className="text-center p-4 bg-green-50 rounded-lg">
                    <CurrencyRupeeIcon className="w-8 h-8 text-green-600 mx-auto mb-2" />
                    <p className="text-sm text-gray-600">Total Profit</p>
                    <p className="text-xl font-bold text-green-600">
                      {formatCurrency(simulationResult.totalProfit)}
                    </p>
                  </div>

                  <div className="text-center p-4 bg-blue-50 rounded-lg">
                    <ChartBarIcon className="w-8 h-8 text-blue-600 mx-auto mb-2" />
                    <p className="text-sm text-gray-600">Efficiency</p>
                    <p className="text-xl font-bold text-blue-600">
                      {simulationResult.efficiencyScore.toFixed(1)}%
                    </p>
                  </div>

                  <div className="text-center p-4 bg-green-50 rounded-lg">
                    <CheckCircleIcon className="w-8 h-8 text-green-600 mx-auto mb-2" />
                    <p className="text-sm text-gray-600">On Time</p>
                    <p className="text-xl font-bold text-green-600">
                      {simulationResult.onTimeDeliveries}
                    </p>
                  </div>

                  <div className="text-center p-4 bg-red-50 rounded-lg">
                    <ExclamationTriangleIcon className="w-8 h-8 text-red-600 mx-auto mb-2" />
                    <p className="text-sm text-gray-600">Late</p>
                    <p className="text-xl font-bold text-red-600">
                      {simulationResult.lateDeliveries}
                    </p>
                  </div>
                </div>
              </div>

              {/* Detailed Breakdown */}
              <div className="bg-white rounded-lg shadow-sm border p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Financial Breakdown</h3>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div className="p-4 border rounded-lg">
                    <p className="text-sm text-gray-600">Total Bonuses</p>
                    <p className="text-lg font-semibold text-green-600">
                      {formatCurrency(simulationResult.totalBonuses)}
                    </p>
                  </div>
                  <div className="p-4 border rounded-lg">
                    <p className="text-sm text-gray-600">Total Penalties</p>
                    <p className="text-lg font-semibold text-red-600">
                      {formatCurrency(simulationResult.totalPenalties)}
                    </p>
                  </div>
                  <div className="p-4 border rounded-lg">
                    <p className="text-sm text-gray-600">Total Fuel Cost</p>
                    <p className="text-lg font-semibold text-orange-600">
                      {formatCurrency(simulationResult.totalFuelCost)}
                    </p>
                  </div>
                </div>

                {/* Fuel Cost Breakdown */}
                {simulationResult.fuelCostBreakdown && Object.keys(simulationResult.fuelCostBreakdown).length > 0 && (
                  <div className="mt-6">
                    <h4 className="text-md font-medium text-gray-900 mb-3">Fuel Cost by Traffic Level</h4>
                    <div className="space-y-2">
                      {Object.entries(simulationResult.fuelCostBreakdown).map(([level, cost]) => (
                        <div key={level} className="flex justify-between items-center py-2 px-3 bg-gray-50 rounded">
                          <span className="text-sm font-medium text-gray-700">{level} Traffic</span>
                          <span className="text-sm font-semibold text-gray-900">{formatCurrency(cost)}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {simulationResult.notes && (
                  <div className="mt-6 p-4 bg-gray-50 rounded-lg">
                    <p className="text-sm text-gray-600">Notes:</p>
                    <p className="text-sm text-gray-900 mt-1">{simulationResult.notes}</p>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="bg-white rounded-lg shadow-sm border p-12 text-center">
              <PlayIcon className="w-16 h-16 text-gray-400 mx-auto mb-4" />
              <h3 className="text-lg font-medium text-gray-900 mb-2">No Simulation Results</h3>
              <p className="text-gray-600">Run a simulation to see results and KPIs here</p>
            </div>
          )}
        </div>
      </div>

      {/* Simulation History */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">My Simulation History</h2>
        {simulationHistory.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Date & Time
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Drivers
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Deliveries
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Efficiency
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Profit
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {simulationHistory.map((sim) => (
                  <tr key={sim.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {formatDateTime(sim.simulationTimestamp)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {sim.numberOfDrivers}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      <div className="flex items-center space-x-2">
                        <span className="text-green-600">{sim.onTimeDeliveries}</span>
                        <span className="text-gray-400">/</span>
                        <span className="text-red-600">{sim.lateDeliveries}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                        sim.efficiencyScore >= 80 ? 'bg-green-100 text-green-800' :
                        sim.efficiencyScore >= 60 ? 'bg-yellow-100 text-yellow-800' :
                        'bg-red-100 text-red-800'
                      }`}>
                        {sim.efficiencyScore.toFixed(1)}%
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {formatCurrency(sim.totalProfit)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-8">
            <ClockIcon className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">No simulation history available</p>
            <p className="text-sm text-gray-500">Your completed simulations will appear here</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Simulation;
