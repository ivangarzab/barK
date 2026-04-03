document.addEventListener("DOMContentLoaded", function () {
  const skip = new Set(["CODE", "PRE", "SCRIPT", "STYLE", "A"]);

  function walk(node) {
    if (node.nodeType === Node.TEXT_NODE) {
      if (!node.textContent.includes("barK")) return;
      const parent = node.parentElement;
      if (!parent || skip.has(parent.tagName)) return;
      const span = document.createElement("span");
      span.innerHTML = node.textContent.replace(/barK/g, "<strong>barK</strong>");
      parent.replaceChild(span, node);
    } else if (node.nodeType === Node.ELEMENT_NODE && !skip.has(node.tagName)) {
      Array.from(node.childNodes).forEach(walk);
    }
  }

  walk(document.body);
});
