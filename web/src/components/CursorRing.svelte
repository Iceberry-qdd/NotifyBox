<script lang="ts">
    import { onMount } from "svelte";

    let cursorX = 0;
    let cursorY = 0;
    let hasPointer = false;
    let pointerPressed = false;

    onMount(() => {
        const moveCursor = (event: PointerEvent) => {
            cursorX = event.clientX;
            cursorY = event.clientY;
            hasPointer = true;
        };
        const leaveWindow = () => (hasPointer = false);
        const pressPointer = () => (pointerPressed = true);
        const releasePointer = () => (pointerPressed = false);

        window.addEventListener("pointermove", moveCursor);
        window.addEventListener("pointerleave", leaveWindow);
        window.addEventListener("pointerdown", pressPointer);
        window.addEventListener("pointerup", releasePointer);
        window.addEventListener("pointercancel", releasePointer);

        return () => {
            window.removeEventListener("pointermove", moveCursor);
            window.removeEventListener("pointerleave", leaveWindow);
            window.removeEventListener("pointerdown", pressPointer);
            window.removeEventListener("pointerup", releasePointer);
            window.removeEventListener("pointercancel", releasePointer);
        };
    });
</script>

<div
    class="cursor-ring"
    class:is-visible={hasPointer}
    class:is-pressed={pointerPressed}
    style={`left:${typeof window === "undefined" ? 0 : (cursorX / window.innerWidth) * 100}%; top:${typeof window === "undefined" ? 0 : (cursorY / window.innerHeight) * 100}%`}
    aria-hidden="true">
</div>

<style>
    .cursor-ring {
        position: fixed;
        z-index: 100;
        width: 3rem;
        height: 3rem;
        border: 2px solid var(--cursor);
        border-radius: 50%;
        pointer-events: none;
        opacity: 0;
        mix-blend-mode: difference;
        transform: translate(-50%, -50%) scale(0.5);
        transition:
            opacity 0.3s ease,
            transform 0.2s ease,
            border-color 0.2s ease;
    }

    .cursor-ring.is-visible {
        opacity: 0.95;
        transform: translate(-50%, -50%) scale(1);
    }

    .cursor-ring.is-pressed {
        opacity: 0.18;
        transform: translate(-50%, -50%) scale(0.12);
    }

    @media (max-width: 640px) {
        .cursor-ring {
            display: none;
        }
    }
</style>
