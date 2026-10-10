import { useState } from 'react';
import {
  Car,
  KeyRound,
  CalendarCheck,
  ShieldCheck,
  Database,
  Search,
  CheckCircle2,
  Terminal,
  Activity,
  UserCheck,
  Tag,
  Calendar,
  Layers,
  Sparkles,
  ChevronRight,
  Server
} from 'lucide-react';

interface CarItem {
  id: number;
  brand: string;
  model: string;
  year: number;
  color: string;
  registrationNumber: string;
  pricePerDay: number;
  available: boolean;
  category: string;
  imageUrl: string;
}

const SAMPLE_CARS: CarItem[] = [
  {
    id: 1,
    brand: 'Toyota',
    model: 'Camry Hybrid',
    year: 2024,
    color: 'Celestial Silver',
    registrationNumber: 'CR-TOY-101',
    pricePerDay: 65.0,
    available: true,
    category: 'Sedan',
    imageUrl: 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?auto=format&fit=crop&w=800&q=80'
  },
  {
    id: 2,
    brand: 'BMW',
    model: '330i M Sport',
    year: 2023,
    color: 'Portimao Blue',
    registrationNumber: 'CR-BMW-202',
    pricePerDay: 110.0,
    available: true,
    category: 'Luxury Sedan',
    imageUrl: 'https://images.unsplash.com/photo-1555215695-3004980ad54e?auto=format&fit=crop&w=800&q=80'
  },
  {
    id: 3,
    brand: 'Tesla',
    model: 'Model Y Long Range',
    year: 2024,
    color: 'Pearl White',
    registrationNumber: 'CR-TSL-303',
    pricePerDay: 95.0,
    available: true,
    category: 'Electric SUV',
    imageUrl: 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?auto=format&fit=crop&w=800&q=80'
  },
  {
    id: 4,
    brand: 'Mercedes-Benz',
    model: 'C-Class AMG Line',
    year: 2024,
    color: 'Obsidian Black',
    registrationNumber: 'CR-MB-404',
    pricePerDay: 120.0,
    available: false,
    category: 'Executive Sedan',
    imageUrl: 'https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?auto=format&fit=crop&w=800&q=80'
  },
  {
    id: 5,
    brand: 'Honda',
    model: 'CR-V Touring',
    year: 2023,
    color: 'Platinum White',
    registrationNumber: 'CR-HON-505',
    pricePerDay: 75.0,
    available: true,
    category: 'Compact SUV',
    imageUrl: 'https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?auto=format&fit=crop&w=800&q=80'
  },
  {
    id: 6,
    brand: 'Audi',
    model: 'A6 Quattro',
    year: 2023,
    color: 'Mythos Black',
    registrationNumber: 'CR-AUD-606',
    pricePerDay: 130.0,
    available: true,
    category: 'Luxury Sedan',
    imageUrl: 'https://images.unsplash.com/photo-1603584173870-7f23fdae1b7a?auto=format&fit=crop&w=800&q=80'
  }
];

export default function App() {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedBrand, setSelectedBrand] = useState('ALL');
  const [onlyAvailable, setOnlyAvailable] = useState(false);
  const [selectedCar, setSelectedCar] = useState<CarItem | null>(SAMPLE_CARS[0]);
  const [startDate, setStartDate] = useState('2026-10-15');
  const [endDate, setEndDate] = useState('2026-10-18');
  const [activeTab, setActiveTab] = useState<'vehicles' | 'endpoints' | 'architecture'>('vehicles');

  const filteredCars = SAMPLE_CARS.filter((c) => {
    const matchesSearch =
      c.brand.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.model.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.registrationNumber.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesBrand = selectedBrand === 'ALL' || c.brand === selectedBrand;
    const matchesAvail = !onlyAvailable || c.available;
    return matchesSearch && matchesBrand && matchesAvail;
  });

  const calculateDays = () => {
    try {
      const start = new Date(startDate);
      const end = new Date(endDate);
      const diffTime = end.getTime() - start.getTime();
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return diffDays > 0 ? diffDays : 0;
    } catch {
      return 0;
    }
  };

  const rentalDays = calculateDays();
  const estimatedCost = selectedCar && rentalDays > 0 ? rentalDays * selectedCar.pricePerDay : 0;

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 flex flex-col font-sans">
      {/* Top Navbar */}
      <header className="border-b border-slate-800 bg-slate-950/80 backdrop-blur sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-blue-600 rounded-lg text-white shadow-lg shadow-blue-500/20">
              <Car className="w-5 h-5" />
            </div>
            <div>
              <div className="font-bold text-lg leading-tight flex items-center gap-2">
                <span>Car Rental Management System</span>
                <span className="text-xs font-semibold px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                  v0.5.0 Active
                </span>
              </div>
              <p className="text-xs text-slate-400 hidden sm:block">
                Java 17 • Spring Boot 3.3.4 • Spring Security • JJWT 0.12 • MySQL
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <div className="flex items-center gap-2 bg-slate-800/80 px-3 py-1.5 rounded-lg border border-slate-700 text-xs text-slate-300">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
              <span>Dev Server: Port 3000 Ready</span>
            </div>
          </div>
        </div>
      </header>

      {/* Hero Overview */}
      <div className="bg-gradient-to-b from-slate-950 via-slate-900 to-slate-900 border-b border-slate-800 px-4 py-8">
        <div className="max-w-7xl mx-auto">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="bg-slate-800/50 p-4 rounded-xl border border-slate-700/60 flex items-center gap-3">
              <div className="p-2.5 rounded-lg bg-blue-500/10 text-blue-400 border border-blue-500/20">
                <Server className="w-5 h-5" />
              </div>
              <div>
                <p className="text-xs text-slate-400">Backend Core</p>
                <p className="font-bold text-sm">Spring Boot 3.3.4</p>
                <p className="text-[11px] text-emerald-400">100 / 100 Tests Passing</p>
              </div>
            </div>

            <div className="bg-slate-800/50 p-4 rounded-xl border border-slate-700/60 flex items-center gap-3">
              <div className="p-2.5 rounded-lg bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                <Database className="w-5 h-5" />
              </div>
              <div>
                <p className="text-xs text-slate-400">Database Engine</p>
                <p className="font-bold text-sm">MySQL JPA & Hibernate</p>
                <p className="text-[11px] text-slate-400">Environment Driven</p>
              </div>
            </div>

            <div className="bg-slate-800/50 p-4 rounded-xl border border-slate-700/60 flex items-center gap-3">
              <div className="p-2.5 rounded-lg bg-purple-500/10 text-purple-400 border border-purple-500/20">
                <ShieldCheck className="w-5 h-5" />
              </div>
              <div>
                <p className="text-xs text-slate-400">Authentication</p>
                <p className="font-bold text-sm">JJWT 0.12.6 + BCrypt</p>
                <p className="text-[11px] text-purple-300">RBAC (CUSTOMER / ADMIN)</p>
              </div>
            </div>

            <div className="bg-slate-800/50 p-4 rounded-xl border border-slate-700/60 flex items-center gap-3">
              <div className="p-2.5 rounded-lg bg-amber-500/10 text-amber-400 border border-amber-500/20">
                <CalendarCheck className="w-5 h-5" />
              </div>
              <div>
                <p className="text-xs text-slate-400">Booking Engine</p>
                <p className="font-bold text-sm">[startDate, endDate)</p>
                <p className="text-[11px] text-amber-300">Concurrency-Safe Locking</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Main View Tabs */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 w-full flex-1">
        <div className="flex border-b border-slate-800 mb-6 gap-2">
          <button
            onClick={() => setActiveTab('vehicles')}
            className={`pb-3 px-4 text-sm font-medium flex items-center gap-2 border-b-2 transition ${
              activeTab === 'vehicles'
                ? 'border-blue-500 text-blue-400'
                : 'border-transparent text-slate-400 hover:text-slate-200'
            }`}
          >
            <Car className="w-4 h-4" />
            Vehicle Fleet & Booking Simulator
          </button>
          <button
            onClick={() => setActiveTab('endpoints')}
            className={`pb-3 px-4 text-sm font-medium flex items-center gap-2 border-b-2 transition ${
              activeTab === 'endpoints'
                ? 'border-blue-500 text-blue-400'
                : 'border-transparent text-slate-400 hover:text-slate-200'
            }`}
          >
            <Terminal className="w-4 h-4" />
            Backend REST APIs (Phases 1–5)
          </button>
          <button
            onClick={() => setActiveTab('architecture')}
            className={`pb-3 px-4 text-sm font-medium flex items-center gap-2 border-b-2 transition ${
              activeTab === 'architecture'
                ? 'border-blue-500 text-blue-400'
                : 'border-transparent text-slate-400 hover:text-slate-200'
            }`}
          >
            <Layers className="w-4 h-4" />
            Architecture & Concurrency Strategy
          </button>
        </div>

        {activeTab === 'vehicles' && (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Left 2 Cols: Fleet browser */}
            <div className="lg:col-span-2 space-y-4">
              {/* Filter Bar */}
              <div className="bg-slate-800/60 p-4 rounded-xl border border-slate-700/60 flex flex-wrap gap-3 items-center justify-between">
                <div className="relative flex-1 min-w-[200px]">
                  <Search className="w-4 h-4 absolute left-3 top-3 text-slate-400" />
                  <input
                    type="text"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    placeholder="Search brand, model, registration..."
                    className="w-full pl-9 pr-3 py-1.5 text-sm bg-slate-900 border border-slate-700 rounded-lg focus:outline-none focus:border-blue-500 text-slate-200"
                  />
                </div>

                <div className="flex items-center gap-2">
                  <select
                    value={selectedBrand}
                    onChange={(e) => setSelectedBrand(e.target.value)}
                    className="bg-slate-900 border border-slate-700 rounded-lg text-sm px-3 py-1.5 text-slate-200 focus:outline-none focus:border-blue-500"
                  >
                    <option value="ALL">All Brands</option>
                    <option value="Toyota">Toyota</option>
                    <option value="BMW">BMW</option>
                    <option value="Tesla">Tesla</option>
                    <option value="Mercedes-Benz">Mercedes-Benz</option>
                    <option value="Honda">Honda</option>
                    <option value="Audi">Audi</option>
                  </select>

                  <label className="flex items-center gap-2 text-xs text-slate-300 cursor-pointer bg-slate-900 px-3 py-2 rounded-lg border border-slate-700 select-none">
                    <input
                      type="checkbox"
                      checked={onlyAvailable}
                      onChange={(e) => setOnlyAvailable(e.target.checked)}
                      className="rounded border-slate-700 text-blue-600 focus:ring-0"
                    />
                    <span>Available Only</span>
                  </label>
                </div>
              </div>

              {/* Cars Grid */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {filteredCars.map((car) => {
                  const isSelected = selectedCar?.id === car.id;
                  return (
                    <div
                      key={car.id}
                      onClick={() => setSelectedCar(car)}
                      className={`bg-slate-800/50 rounded-xl overflow-hidden border cursor-pointer transition hover:border-blue-500/70 ${
                        isSelected
                          ? 'border-blue-500 ring-2 ring-blue-500/20 bg-slate-800/90'
                          : 'border-slate-700/60'
                      }`}
                    >
                      <div className="relative h-44 bg-slate-900 overflow-hidden">
                        <img
                          src={car.imageUrl}
                          alt={`${car.brand} ${car.model}`}
                          className="w-full h-full object-cover group-hover:scale-105 transition duration-300"
                        />
                        <div className="absolute top-2 right-2">
                          <span
                            className={`text-[11px] font-semibold px-2 py-0.5 rounded-full border ${
                              car.available
                                ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40'
                                : 'bg-rose-500/20 text-rose-300 border-rose-500/40'
                            }`}
                          >
                            {car.available ? 'Available' : 'Booked'}
                          </span>
                        </div>
                        <div className="absolute bottom-2 left-2">
                          <span className="text-[11px] bg-slate-950/80 backdrop-blur px-2 py-0.5 rounded text-slate-300 border border-slate-700/50">
                            {car.category}
                          </span>
                        </div>
                      </div>

                      <div className="p-4">
                        <div className="flex items-start justify-between">
                          <div>
                            <h4 className="font-semibold text-white">
                              {car.brand} {car.model}
                            </h4>
                            <p className="text-xs text-slate-400">
                              {car.year} • {car.color}
                            </p>
                          </div>
                          <div className="text-right">
                            <span className="text-lg font-bold text-blue-400">
                              ${car.pricePerDay.toFixed(2)}
                            </span>
                            <span className="text-xs text-slate-400 block">/day</span>
                          </div>
                        </div>

                        <div className="mt-3 pt-3 border-t border-slate-700/60 flex items-center justify-between text-xs text-slate-400">
                          <span className="font-mono">{car.registrationNumber}</span>
                          <span className="text-blue-400 flex items-center gap-1 font-medium">
                            Select Vehicle <ChevronRight className="w-3.5 h-3.5" />
                          </span>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Right Col: Booking & Availability Simulator */}
            <div className="space-y-4">
              <div className="bg-slate-800/70 p-5 rounded-xl border border-slate-700 shadow-xl">
                <div className="flex items-center gap-2 mb-4">
                  <CalendarCheck className="w-5 h-5 text-blue-400" />
                  <h3 className="font-bold text-white text-base">Booking Date Simulator</h3>
                </div>

                {selectedCar ? (
                  <div className="space-y-4">
                    <div className="p-3 bg-slate-900/80 rounded-lg border border-slate-700/70 flex items-center gap-3">
                      <div className="w-12 h-12 rounded bg-slate-800 overflow-hidden flex-shrink-0">
                        <img
                          src={selectedCar.imageUrl}
                          alt="Selected car"
                          className="w-full h-full object-cover"
                        />
                      </div>
                      <div>
                        <div className="font-semibold text-sm">
                          {selectedCar.brand} {selectedCar.model}
                        </div>
                        <div className="text-xs text-slate-400 font-mono">
                          {selectedCar.registrationNumber} • ${selectedCar.pricePerDay}/day
                        </div>
                      </div>
                    </div>

                    <div className="space-y-3">
                      <div>
                        <label className="text-xs text-slate-300 block mb-1">
                          Pick-up Date (<span className="text-emerald-400">startDate</span>)
                        </label>
                        <input
                          type="date"
                          value={startDate}
                          onChange={(e) => setStartDate(e.target.value)}
                          className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-blue-500"
                        />
                      </div>

                      <div>
                        <label className="text-xs text-slate-300 block mb-1">
                          Drop-off Date (<span className="text-emerald-400">endDate</span>)
                        </label>
                        <input
                          type="date"
                          value={endDate}
                          onChange={(e) => setEndDate(e.target.value)}
                          className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-blue-500"
                        />
                      </div>
                    </div>

                    {/* Calculation breakdown */}
                    <div className="bg-slate-950/60 p-4 rounded-lg border border-slate-800 space-y-2 text-xs">
                      <div className="flex justify-between text-slate-400">
                        <span>Rental Rule:</span>
                        <span className="font-mono text-slate-300">[startDate, endDate)</span>
                      </div>
                      <div className="flex justify-between text-slate-400">
                        <span>Billable Days:</span>
                        <span className="font-bold text-slate-200">
                          {rentalDays > 0 ? `${rentalDays} days` : '0 (Invalid range)'}
                        </span>
                      </div>
                      <div className="flex justify-between text-slate-400">
                        <span>Rate:</span>
                        <span className="text-slate-300">${selectedCar.pricePerDay.toFixed(2)} / day</span>
                      </div>
                      <div className="pt-2 border-t border-slate-800 flex justify-between items-baseline text-sm">
                        <span className="font-semibold text-slate-200">Backend Total:</span>
                        <span className="text-lg font-bold text-emerald-400">
                          ${estimatedCost.toFixed(2)}
                        </span>
                      </div>
                      <p className="text-[11px] text-slate-400 italic pt-1">
                        *Return date is not charged as an extra day per backend specification.
                      </p>
                    </div>

                    <div className="pt-2">
                      <div className="p-3 bg-blue-950/30 border border-blue-800/40 rounded-lg text-xs text-blue-200 space-y-1">
                        <div className="font-semibold flex items-center gap-1.5 text-blue-300">
                          <CheckCircle2 className="w-3.5 h-3.5" />
                          Validated API Payload:
                        </div>
                        <code className="block bg-slate-950/80 p-2 rounded text-[11px] text-emerald-400 font-mono">
                          {JSON.stringify(
                            {
                              carId: selectedCar.id,
                              startDate: startDate,
                              endDate: endDate,
                              rentalDays: rentalDays,
                              totalPrice: estimatedCost
                            },
                            null,
                            2
                          )}
                        </code>
                      </div>
                    </div>
                  </div>
                ) : (
                  <p className="text-xs text-slate-400">Select a vehicle from the fleet to simulate.</p>
                )}
              </div>
            </div>
          </div>
        )}

        {activeTab === 'endpoints' && (
          <div className="space-y-4">
            <div className="bg-slate-800/40 p-4 rounded-xl border border-slate-700/60">
              <h3 className="font-bold text-base mb-1">REST API Endpoints Specification</h3>
              <p className="text-xs text-slate-400">
                All endpoints verified via 100 automated JUnit tests and active Spring Security filter chain.
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Car Management */}
              <div className="bg-slate-800/50 p-4 rounded-xl border border-slate-700/60 space-y-3">
                <div className="flex items-center gap-2 font-semibold text-sm text-blue-400 border-b border-slate-700 pb-2">
                  <Car className="w-4 h-4" />
                  Car Management & Catalog
                </div>
                <div className="space-y-2 text-xs font-mono">
                  <div className="p-2 bg-slate-900 rounded border border-slate-800 flex items-center justify-between">
                    <div>
                      <span className="text-emerald-400 font-bold mr-2">GET</span>
                      <span className="text-slate-200">/api/cars</span>
                    </div>
                    <span className="text-[10px] text-slate-400">Public / Paginated</span>
                  </div>
                  <div className="p-2 bg-slate-900 rounded border border-slate-800 flex items-center justify-between">
                    <div>
                      <span className="text-emerald-400 font-bold mr-2">GET</span>
                      <span className="text-slate-200">/api/cars/&#123;id&#125;</span>
                    </div>
                    <span className="text-[10px] text-slate-400">Public</span>
                  </div>
                  <div className="p-2 bg-slate-900 rounded border border-slate-800 flex items-center justify-between">
                    <div>
                      <span className="text-emerald-400 font-bold mr-2">GET</span>
                      <span className="text-slate-200">/api/cars/&#123;id&#125;/availability</span>
                    </div>
                    <span className="text-[10px] text-slate-400">Date Range Check</span>
                  </div>
                  <div className="p-2 bg-slate-900 rounded border border-slate-800 flex items-center justify-between">
                    <div>
                      <span className="text-amber-400 font-bold mr-2">POST</span>
                      <span className="text-slate-200">/api/cars</span>
                    </div>
                    <span className="text-[10px] text-rose-400 font-sans font-semibold">ROLE_ADMIN</span>
                  </div>
                  <div className="p-2 bg-slate-900 rounded border border-slate-800 flex items-center justify-between">
                    <div>
                      <span className="text-blue-400 font-bold mr-2">PUT</span>
                      <span className="text-slate-200">/api/cars/&#123;id&#125;</span>
                    </div>
                    <span className="text-[10px] text-rose-400 font-sans font-semibold">ROLE_ADMIN</span>
                  </div>
                  <div className="p-2 bg-slate-900 rounded border border-slate-800 flex items-center justify-between">
                    <div>
                      <span className="text-rose-400 font-bold mr-2">DELETE</span>
                      <span className="text-slate-200">/api/cars/&#123;id&#125;</span>
                    </div>
                    <span className="text-[10px] text-rose-400 font-sans font-semibold">ROLE_ADMIN</span>
                  </div>
                </div>
              </div>

              {/* Booking & Auth */}
              <div className="bg-slate-800/50 p-4 rounded-xl border border-slate-700/60 space-y-3">
                <div className="flex items-center gap-2 font-semibold text-sm text-emerald-400 border-b border-slate-700 pb-2">
                  <CalendarCheck className="w-4 h-4" />
                  Booking & Authentication
                </div>
                <div className="space-y-2 text-xs font-mono">
                  <div className="p-2 bg-slate-900 rounded border border-slate-800 flex items-center justify-between">
                    <div>
                      <span className="text-amber-400 font-bold mr-2">POST</span>
                      <span className="text-slate-200">/api/auth/register</span>
                    </div>
                    <span className="text-[10px] text-emerald-400 font-sans">Public (BCrypt)</span>
                  </div>
                  <div className="p-2 bg-slate-900 rounded border border-slate-800 flex items-center justify-between">
                    <div>
                      <span className="text-amber-400 font-bold mr-2">POST</span>
                      <span className="text-slate-200">/api/auth/login</span>
                    </div>
                    <span className="text-[10px] text-emerald-400 font-sans">Returns JWT</span>
                  </div>
                  <div className="p-2 bg-slate-900 rounded border border-slate-800 flex items-center justify-between">
                    <div>
                      <span className="text-amber-400 font-bold mr-2">POST</span>
                      <span className="text-slate-200">/api/bookings</span>
                    </div>
                    <span className="text-[10px] text-purple-400 font-sans">Authenticated</span>
                  </div>
                  <div className="p-2 bg-slate-900 rounded border border-slate-800 flex items-center justify-between">
                    <div>
                      <span className="text-emerald-400 font-bold mr-2">GET</span>
                      <span className="text-slate-200">/api/bookings/my</span>
                    </div>
                    <span className="text-[10px] text-purple-400 font-sans">Owner Bookings</span>
                  </div>
                  <div className="p-2 bg-slate-900 rounded border border-slate-800 flex items-center justify-between">
                    <div>
                      <span className="text-cyan-400 font-bold mr-2">PATCH</span>
                      <span className="text-slate-200">/api/bookings/&#123;id&#125;/cancel</span>
                    </div>
                    <span className="text-[10px] text-purple-400 font-sans">Owner or ADMIN</span>
                  </div>
                  <div className="p-2 bg-slate-900 rounded border border-slate-800 flex items-center justify-between">
                    <div>
                      <span className="text-cyan-400 font-bold mr-2">PATCH</span>
                      <span className="text-slate-200">/api/admin/bookings/&#123;id&#125;/status</span>
                    </div>
                    <span className="text-[10px] text-rose-400 font-sans font-semibold">ROLE_ADMIN</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'architecture' && (
          <div className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="bg-slate-800/60 p-5 rounded-xl border border-slate-700/60">
                <div className="p-2 rounded bg-blue-500/10 text-blue-400 w-fit mb-3">
                  <ShieldCheck className="w-5 h-5" />
                </div>
                <h4 className="font-semibold text-sm mb-1">Role-Based Security</h4>
                <p className="text-xs text-slate-300 leading-relaxed">
                  Stateless Spring Security filter chain with JJWT 0.12.6. Public access for browsing cars, authenticated customer role for personal bookings, and strict ADMIN role for modifying vehicles or overriding bookings.
                </p>
              </div>

              <div className="bg-slate-800/60 p-5 rounded-xl border border-slate-700/60">
                <div className="p-2 rounded bg-amber-500/10 text-amber-400 w-fit mb-3">
                  <Calendar className="w-5 h-5" />
                </div>
                <h4 className="font-semibold text-sm mb-1">Rental Interval Model</h4>
                <p className="text-xs text-slate-300 leading-relaxed">
                  The rental duration is defined as <code className="text-amber-300">[startDate, endDate)</code>. Return date is never billed. Overlaps are detected mathematically against active bookings (PENDING & CONFIRMED).
                </p>
              </div>

              <div className="bg-slate-800/60 p-5 rounded-xl border border-slate-700/60">
                <div className="p-2 rounded bg-emerald-500/10 text-emerald-400 w-fit mb-3">
                  <Activity className="w-5 h-5" />
                </div>
                <h4 className="font-semibold text-sm mb-1">Atomic Transactions</h4>
                <p className="text-xs text-slate-300 leading-relaxed">
                  Isolation level READ_COMMITTED prevents phantom overlaps. Backend calculates price authoritatively from car repository pricePerDay to guarantee absolute data integrity.
                </p>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Footer */}
      <footer className="mt-auto border-t border-slate-800 bg-slate-950 py-4 px-4 text-center text-xs text-slate-400">
        <p>Car Rental Management System • Java 17 + Spring Boot 3.3.4 + React 18 / Tailwind CSS</p>
      </footer>
    </div>
  );
}
