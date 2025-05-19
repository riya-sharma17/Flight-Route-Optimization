const graph = {
  cities: new Set(),
  flights: {}
};

function addCity() {
  const city = document.getElementById('cityName').value.trim();
  if (!city) return alert('Enter city name');
  if (graph.cities.has(city)) {
    appendOutput(`City "${city}" already exists.`);
  } else {
    graph.cities.add(city);
    appendOutput(`City "${city}" added.`);
  }
  document.getElementById('cityName').value = '';
}

function addFlight() {
  const from = document.getElementById('fromCity').value.trim();
  const to = document.getElementById('toCity').value.trim();
  const duration = parseInt(document.getElementById('duration').value.trim());
  const cost = parseFloat(document.getElementById('cost').value.trim());

  if (!from || !to || isNaN(duration) || isNaN(cost)) {
    return alert('Fill all flight fields with valid values');
  }
  if (!graph.cities.has(from) || !graph.cities.has(to)) {
    return alert('Both cities must be added first');
  }

  if (!graph.flights[from]) graph.flights[from] = [];
  graph.flights[from].push({ to, duration, cost });

  appendOutput(`Flight added from "${from}" to "${to}"`);

  document.getElementById('fromCity').value = '';
  document.getElementById('toCity').value = '';
  document.getElementById('duration').value = '';
  document.getElementById('cost').value = '';
}

function showRoutes() {
  let out = 'Cities:\n';
  graph.cities.forEach(city => (out += '- ' + city + '\n'));
  out += '\nFlights:\n';
  for (const from in graph.flights) {
    graph.flights[from].forEach(flight => {
      out += `From ${from} to ${flight.to}: Duration ${flight.duration} min, Cost ${flight.cost}\n`;
    });
  }
  appendOutput(out);
}

function appendOutput(text) {
  const output = document.getElementById('output');
  output.textContent += text + '\n\n';
  output.scrollTop = output.scrollHeight;
}

function handleRoute() {
  const start = document.getElementById('startCity').value.trim();
  const end = document.getElementById('endCity').value.trim();
  const mode = document.getElementById('routeMode').value;
  findRoute(start, end, mode);
}

function findRoute(start, end, mode) {
  if (!graph.cities.has(start) || !graph.cities.has(end)) {
    appendOutput('Start and End cities must exist.');
    return;
  }

  const distances = {};
  const prev = {};
  const pq = new MinPriorityQueue();

  graph.cities.forEach(city => {
    distances[city] = Infinity;
    prev[city] = null;
  });
  distances[start] = 0;
  pq.enqueue(start, 0);

  while (!pq.isEmpty()) {
    const { element: current } = pq.dequeue();

    if (current === end) break;

    const neighbors = graph.flights[current] || [];
    neighbors.forEach(({ to, cost, duration }) => {
      const weight = mode === 'cost' ? cost : duration;
      const alt = distances[current] + weight;
      if (alt < distances[to]) {
        distances[to] = alt;
        prev[to] = current;
        pq.enqueue(to, alt);
      }
    });
  }

  if (distances[end] === Infinity) {
    appendOutput(`No route found from "${start}" to "${end}".`);
    return;
  }

  const path = [];
  let at = end;
  while (at) {
    path.push(at);
    at = prev[at];
  }
  path.reverse();

  appendOutput(
    `${mode === 'cost' ? 'Cheapest' : 'Fastest'} route from "${start}" to "${end}":\n` +
    path.join(' -> ') +
    `\nTotal ${mode === 'cost' ? 'Cost' : 'Duration'}: ${distances[end]}`
  );
}

class MinPriorityQueue {
  constructor() {
    this.items = [];
  }

  enqueue(element, priority) {
    this.items.push({ element, priority });
    this.items.sort((a, b) => a.priority - b.priority);
  }

  dequeue() {
    return this.items.shift();
  }

  isEmpty() {
    return this.items.length === 0;
  }
}
