import React, { useState, useEffect } from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';
import { 
  PlusIcon, 
  PencilIcon, 
  TrashIcon,
  MapIcon,
  ClockIcon,
  TruckIcon,
  ExclamationTriangleIcon,
  CheckCircleIcon
} from '@heroicons/react/outline';
import LoadingSpinner from '../components/LoadingSpinner';

const Routes = () => {
  const [routes, setRoutes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingRoute, setEditingRoute] = useState(null);
  const [formData, setFormData] = useState({
    routeId: '',
    distanceKm: '',
    trafficLevel: 'Low',
    baseTimeMinutes: '',
    startLocation: '',
    endLocation: '',
    isActive: true
  });

  useEffect(() => {
    fetchRoutes();
  }, []);

  const fetchRoutes = async () => {
    try {
      setLoading(true);
      const response = await axios.get('/routes');
      setRoutes(response.data);
    } catch (error) {
      console.error('Error fetching routes:', error);
      toast.error('Failed to load routes');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = {
        ...formData,
        distanceKm: parseFloat(formData.distanceKm),
        baseTimeMinutes: parseInt(formData.baseTimeMinutes)
      };

      if (editingRoute) {
        await axios.put(`/routes/${editingRoute.id}`, data);
        toast.success('Route updated successfully');
      } else {
        await axios.post('/routes', data);
        toast.success('Route created successfully');
      }

      setShowModal(false);
      setEditingRoute(null);
      resetForm();
      fetchRoutes();
    } catch (error) {
      console.error('Error saving route:', error);
      toast.error('Failed to save route');
    }
  };

  const handleEdit = (route) => {
    setEditingRoute(route);
    setFormData({
      routeId: route.routeId,
      distanceKm: route.distanceKm.toString(),
      trafficLevel: route.trafficLevel,
      baseTimeMinutes: route.baseTimeMinutes.toString(),
      startLocation: route.startLocation || '',
      endLocation: route.endLocation || '',
      isActive: route.active
    });
    setShowModal(true);
  };

  const handleDelete = async (route) => {
    if (window.confirm(`Are you sure you want to delete route ${route.routeId}?`)) {
      try {
        await axios.delete(`/routes/${route.id}`);
        toast.success('Route deleted successfully');
        fetchRoutes();
      } catch (error) {
        console.error('Error deleting route:', error);
        toast.error('Failed to delete route');
      }
    }
  };

  const resetForm = () => {
    setFormData({
      routeId: '',
      distanceKm: '',
      trafficLevel: 'Low',
      baseTimeMinutes: '',
      startLocation: '',
      endLocation: '',
      isActive: true
    });
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingRoute(null);
    resetForm();
  };

  const getTrafficLevelColor = (level) => {
    switch (level) {
      case 'Low': return 'bg-green-100 text-green-800';
      case 'Medium': return 'bg-yellow-100 text-yellow-800';
      case 'High': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const calculateFuelCost = (distanceKm, trafficLevel) => {
    const baseCost = distanceKm * 5; // ₹5/km
    const surcharge = trafficLevel === 'High' ? distanceKm * 2 : 0; // +₹2/km for high traffic
    return baseCost + surcharge;
  };

  if (loading) {
    return <LoadingSpinner text="Loading routes..." />;
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Routes Management</h1>
            <p className="text-gray-600">Manage delivery routes and traffic conditions</p>
          </div>
          <button
            onClick={() => setShowModal(true)}
            className="flex items-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
          >
            <PlusIcon className="w-5 h-5 mr-2" />
            Add Route
          </button>
        </div>
      </div>

      {/* Routes Table */}
      <div className="bg-white rounded-lg shadow-sm border overflow-hidden">
        {routes.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Route
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Distance
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Traffic Level
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Base Time
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Fuel Cost
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {routes.map((route) => (
                  <tr key={route.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <MapIcon className="w-8 h-8 text-gray-400 mr-3" />
                        <div>
                          <div className="text-sm font-medium text-gray-900">
                            {route.routeId}
                          </div>
                          <div className="text-sm text-gray-500">
                            {route.startLocation && route.endLocation ? (
                              `${route.startLocation} → ${route.endLocation}`
                            ) : (
                              'No locations specified'
                            )}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {route.distanceKm} km
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getTrafficLevelColor(route.trafficLevel)}`}>
                        {route.trafficLevel}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      <div className="flex items-center">
                        <ClockIcon className="w-4 h-4 text-gray-400 mr-1" />
                        {route.baseTimeMinutes} min
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      ₹{calculateFuelCost(route.distanceKm, route.trafficLevel).toFixed(2)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        route.active 
                          ? 'bg-green-100 text-green-800' 
                          : 'bg-red-100 text-red-800'
                      }`}>
                        {route.active ? (
                          <>
                            <CheckCircleIcon className="w-4 h-4 mr-1" />
                            Active
                          </>
                        ) : (
                          <>
                            <ExclamationTriangleIcon className="w-4 h-4 mr-1" />
                            Inactive
                          </>
                        )}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                      <button
                        onClick={() => handleEdit(route)}
                        className="text-indigo-600 hover:text-indigo-900 inline-flex items-center"
                      >
                        <PencilIcon className="w-4 h-4 mr-1" />
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(route)}
                        className="text-red-600 hover:text-red-900 inline-flex items-center"
                      >
                        <TrashIcon className="w-4 h-4 mr-1" />
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-12">
            <MapIcon className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">No Routes Found</h3>
            <p className="text-gray-600 mb-4">Get started by adding your first delivery route</p>
            <button
              onClick={() => setShowModal(true)}
              className="inline-flex items-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
            >
              <PlusIcon className="w-5 h-5 mr-2" />
              Add Route
            </button>
          </div>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg font-medium text-gray-900 mb-4">
                {editingRoute ? 'Edit Route' : 'Add New Route'}
              </h3>
              
              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Route ID *
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.routeId}
                    onChange={(e) => setFormData({...formData, routeId: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-primary-500 focus:border-primary-500"
                    placeholder="e.g., R001"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Distance (km) *
                  </label>
                  <input
                    type="number"
                    step="0.1"
                    min="0"
                    required
                    value={formData.distanceKm}
                    onChange={(e) => setFormData({...formData, distanceKm: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-primary-500 focus:border-primary-500"
                    placeholder="0.0"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Traffic Level *
                  </label>
                  <select
                    required
                    value={formData.trafficLevel}
                    onChange={(e) => setFormData({...formData, trafficLevel: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-primary-500 focus:border-primary-500"
                  >
                    <option value="Low">Low</option>
                    <option value="Medium">Medium</option>
                    <option value="High">High</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Base Time (minutes) *
                  </label>
                  <input
                    type="number"
                    min="1"
                    required
                    value={formData.baseTimeMinutes}
                    onChange={(e) => setFormData({...formData, baseTimeMinutes: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-primary-500 focus:border-primary-500"
                    placeholder="30"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Start Location
                  </label>
                  <input
                    type="text"
                    value={formData.startLocation}
                    onChange={(e) => setFormData({...formData, startLocation: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-primary-500 focus:border-primary-500"
                    placeholder="e.g., Warehouse A"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    End Location
                  </label>
                  <input
                    type="text"
                    value={formData.endLocation}
                    onChange={(e) => setFormData({...formData, endLocation: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-primary-500 focus:border-primary-500"
                    placeholder="e.g., Customer Location"
                  />
                </div>

                <div className="flex items-center">
                  <input
                    type="checkbox"
                    checked={formData.isActive}
                    onChange={(e) => setFormData({...formData, isActive: e.target.checked})}
                    className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                  />
                  <label className="ml-2 block text-sm text-gray-900">
                    Active Route
                  </label>
                </div>

                {/* Fuel Cost Preview */}
                {formData.distanceKm && (
                  <div className="p-3 bg-blue-50 rounded-lg">
                    <p className="text-sm text-blue-800">
                      <strong>Estimated Fuel Cost:</strong> ₹
                      {calculateFuelCost(
                        parseFloat(formData.distanceKm) || 0, 
                        formData.trafficLevel
                      ).toFixed(2)}
                    </p>
                    <p className="text-xs text-blue-600 mt-1">
                      Base: ₹{((parseFloat(formData.distanceKm) || 0) * 5).toFixed(2)} 
                      {formData.trafficLevel === 'High' && 
                        ` + Surcharge: ₹${((parseFloat(formData.distanceKm) || 0) * 2).toFixed(2)}`
                      }
                    </p>
                  </div>
                )}

                <div className="flex justify-end space-x-3 pt-4">
                  <button
                    type="button"
                    onClick={handleCloseModal}
                    className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 rounded-lg transition-colors"
                  >
                    {editingRoute ? 'Update' : 'Create'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Routes;
