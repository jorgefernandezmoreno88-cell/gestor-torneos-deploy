import { c as createComponent } from './astro-component_0s_TtBMU.mjs';
import 'piccolore';
import { m as maybeRenderHead, h as addAttribute, r as renderTemplate } from './entrypoint_BgRex8wb.mjs';
import 'clsx';

const $$TorneoCard = createComponent(($$result, $$props, $$slots) => {
  const Astro2 = $$result.createAstro($$props, $$slots);
  Astro2.self = $$TorneoCard;
  const { torneo } = Astro2.props;
  const getStatusColor = (status) => {
    switch (status) {
      case "ABIERTO":
        return "status-open";
      case "EN_PROGRESO":
        return "status-in-progress";
      case "CERRADO":
      case "FINALIZADO":
        return "status-closed";
      default:
        return "";
    }
  };
  const formatDate = (dateString) => {
    if (!dateString) return "";
    return new Date(dateString).toLocaleDateString("es-ES");
  };
  return renderTemplate`${maybeRenderHead()}<article class="torneo-card card" data-astro-cid-pwlrtcjj> <div class="card-header" data-astro-cid-pwlrtcjj> <div data-astro-cid-pwlrtcjj> <h3 data-astro-cid-pwlrtcjj><a${addAttribute(`/torneos/${torneo.id}`, "href")} data-astro-cid-pwlrtcjj>${torneo.nombre}</a></h3> <p class="nivel" data-astro-cid-pwlrtcjj>${torneo.nivel}</p> </div> <span${addAttribute(`status ${getStatusColor(torneo.estado)}`, "class")} data-astro-cid-pwlrtcjj> ${torneo.estado} </span> </div> ${torneo.descripcion && renderTemplate`<p class="description" data-astro-cid-pwlrtcjj>${torneo.descripcion}</p>`} <div class="card-meta" data-astro-cid-pwlrtcjj> ${torneo.fechaInicio && renderTemplate`<div class="meta-item" data-astro-cid-pwlrtcjj> <span class="icon" data-astro-cid-pwlrtcjj>📅</span> <span data-astro-cid-pwlrtcjj>${formatDate(torneo.fechaInicio)}</span> </div>`} <div class="meta-item" data-astro-cid-pwlrtcjj> <span class="icon" data-astro-cid-pwlrtcjj>👥</span> <span data-astro-cid-pwlrtcjj>${torneo.plazasMaximas} participantes</span> </div> <div class="meta-item" data-astro-cid-pwlrtcjj> <span class="icon" data-astro-cid-pwlrtcjj>🔄</span> <span data-astro-cid-pwlrtcjj>${torneo.numRondas} rondas</span> </div> </div> <div class="card-footer" data-astro-cid-pwlrtcjj> <a${addAttribute(`/torneos/${torneo.id}`, "href")} class="button button-primary" data-astro-cid-pwlrtcjj>
Ver Detalles
</a> </div> </article>`;
}, "D:/Descargas/gestor-torneos-clase-master/frontend/src/components/torneos/TorneoCard.astro", void 0);

export { $$TorneoCard as $ };
