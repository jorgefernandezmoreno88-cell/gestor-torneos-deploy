import { c as createComponent } from './astro-component_0s_TtBMU.mjs';
import 'piccolore';
import { r as renderTemplate, n as defineScriptVars, l as renderComponent, m as maybeRenderHead } from './entrypoint_BgRex8wb.mjs';
import { $ as $$MainLayout } from './MainLayout_pCS74QOT.mjs';
import { A as API_BASE_URL } from './api_CYCyv3yI.mjs';

var __freeze = Object.freeze;
var __defProp = Object.defineProperty;
var __template = (cooked, raw) => __freeze(__defProp(cooked, "raw", { value: __freeze(raw || cooked.slice()) }));
var _a;
const prerender = false;
const $$Resumen = createComponent(async ($$result, $$props, $$slots) => {
  const Astro2 = $$result.createAstro($$props, $$slots);
  Astro2.self = $$Resumen;
  const { id } = Astro2.params;
  const torneoId = Number(id);
  return renderTemplate(_a || (_a = __template(["", ' <script type="module">', "\n  const TOKEN_KEY = 'authToken';\n\n  function showMessage(text, level = 'error') {\n    const messageBox = document.getElementById('resumen-message');\n    if (!messageBox) return;\n    messageBox.className = `alert alert-${level}`;\n    messageBox.textContent = text;\n    messageBox.style.display = 'block';\n  }\n\n  document.addEventListener('DOMContentLoaded', async () => {\n    const content = document.getElementById('resumen-content');\n    if (!content) return;\n    if (!Number.isFinite(Number(torneoId))) {\n      showMessage('Torneo no encontrado');\n      content.textContent = '';\n      return;\n    }\n\n    const token = localStorage.getItem(TOKEN_KEY);\n    const headers = token ? { Authorization: `Bearer ${token}` } : {};\n\n    try {\n      const response = await fetch(`${apiBaseUrl}/torneos/${torneoId}/resumen`, { headers });\n      if (!response.ok) {\n        throw new Error('Torneo no encontrado');\n      }\n      const resumen = await response.json();\n      content.textContent = JSON.stringify(resumen, null, 2);\n    } catch (error) {\n      content.textContent = '';\n      showMessage(error instanceof Error ? error.message : 'Torneo no encontrado');\n    }\n  });\n<\/script>"], ["", ' <script type="module">', "\n  const TOKEN_KEY = 'authToken';\n\n  function showMessage(text, level = 'error') {\n    const messageBox = document.getElementById('resumen-message');\n    if (!messageBox) return;\n    messageBox.className = \\`alert alert-\\${level}\\`;\n    messageBox.textContent = text;\n    messageBox.style.display = 'block';\n  }\n\n  document.addEventListener('DOMContentLoaded', async () => {\n    const content = document.getElementById('resumen-content');\n    if (!content) return;\n    if (!Number.isFinite(Number(torneoId))) {\n      showMessage('Torneo no encontrado');\n      content.textContent = '';\n      return;\n    }\n\n    const token = localStorage.getItem(TOKEN_KEY);\n    const headers = token ? { Authorization: \\`Bearer \\${token}\\` } : {};\n\n    try {\n      const response = await fetch(\\`\\${apiBaseUrl}/torneos/\\${torneoId}/resumen\\`, { headers });\n      if (!response.ok) {\n        throw new Error('Torneo no encontrado');\n      }\n      const resumen = await response.json();\n      content.textContent = JSON.stringify(resumen, null, 2);\n    } catch (error) {\n      content.textContent = '';\n      showMessage(error instanceof Error ? error.message : 'Torneo no encontrado');\n    }\n  });\n<\/script>"])), renderComponent($$result, "MainLayout", $$MainLayout, { "title": "Resumen de Torneo - Gestor de Torneos", "description": "Resumen del torneo", "data-astro-cid-omev5k4e": true }, { "default": async ($$result2) => renderTemplate` ${maybeRenderHead()}<div class="container" data-astro-cid-omev5k4e> <h1 data-astro-cid-omev5k4e>Resumen del torneo #${id}</h1> <div id="resumen-message" class="alert" style="display:none;" data-astro-cid-omev5k4e></div> <pre id="resumen-content" class="card resumen-pre" data-astro-cid-omev5k4e>Cargando resumen...</pre> </div> ` }), defineScriptVars({ apiBaseUrl: API_BASE_URL, torneoId }));
}, "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/torneos/[id]/resumen.astro", void 0);

const $$file = "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/torneos/[id]/resumen.astro";
const $$url = "/torneos/[id]/resumen";

const _page = /*#__PURE__*/Object.freeze(/*#__PURE__*/Object.defineProperty({
  __proto__: null,
  default: $$Resumen,
  file: $$file,
  prerender,
  url: $$url
}, Symbol.toStringTag, { value: 'Module' }));

const page = () => _page;

export { page };
