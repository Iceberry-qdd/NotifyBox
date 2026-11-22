<script lang="ts">
    import { onDestroy, tick } from "svelte";

    export let title = "";
    export let rows: readonly (readonly string[])[] = [];
    export let active = false;

    const characterInterval = 8;
    const fieldPause = 32;
    const linePause = 48;
    let displayedRows: string[][] = [];
    let previousActive = false;
    let previousRows = rows;
    let runId = 0;

    let logContainer: HTMLDivElement | undefined = undefined;

    function wait(milliseconds: number) {
        return new Promise<void>((resolve) => {
            window.setTimeout(resolve, milliseconds);
        });
    }

    async function typeRows(sequence: number, sourceRows: readonly (readonly string[])[]) {
        displayedRows = [];

        for (const sourceRow of sourceRows) {
            if (sequence !== runId) return;

            const currentRow = sourceRow.map(() => "");
            displayedRows = [...displayedRows, currentRow];

            await tick();
            if (logContainer) {
                logContainer.scrollTop = logContainer.scrollHeight;
            }

            for (let fieldIndex = 0; fieldIndex < sourceRow.length; fieldIndex += 1) {
                for (const [characterIndex, character] of [...sourceRow[fieldIndex]].entries()) {
                    if (sequence !== runId) return;
                    if (characterIndex > 0) await wait(characterInterval);

                    currentRow[fieldIndex] += character;
                    displayedRows = displayedRows.map((row, index) => (index === displayedRows.length - 1 ? [...currentRow] : row));
                }
                await wait(fieldPause);
            }

            await wait(linePause);
        }
    }

    function restartTyping() {
        runId += 1;
        if (active) void typeRows(runId, rows);
        else displayedRows = [];
    }

    $: if (active !== previousActive || rows !== previousRows) {
        previousActive = active;
        previousRows = rows;
        restartTyping();
    }

    onDestroy(() => {
        runId += 1;
    });
</script>

<div
    class="log-window"
    class:is-active={active}>
    <div class="window-top">
        <span class="window-lights"><i></i><i></i><i></i></span><span>{title}</span>
    </div>
    <div class="log-window-body">
        <div
            class="log-stream"
            bind:this={logContainer}>
            {#each displayedRows as row}
                <div
                    class="log-row"
                    class:warn={row[1] === "WARN"}>
                    <span class="log-time log-cell">{row[0]}</span>
                    <span class="log-level log-cell">{row[1]}</span>
                    <span class="log-source log-cell">{row[2]}</span>
                </div>
            {/each}
        </div>
    </div>
</div>

<style>
    .log-window {
        position: absolute;
        width: 36rem;
        height: 21.25rem;
        left: 0;
        top: 70%;
        transform: translateY(-50%) rotate(-2deg);
        background: color-mix(in srgb, var(--surface-solid) 92%, transparent);
        box-shadow: var(--shadow);
        border-radius: 1rem;
        z-index: 3;
        overflow: hidden;
    }
    .window-top {
        height: 2.5rem;
        border-bottom: 0.0625rem solid var(--line);
        display: flex;
        align-items: center;
        padding: 0 0.9375rem;
        color: var(--muted);
        font-family: var(--mono);
        font-size: 0.625rem;
    }
    .window-lights {
        display: flex;
        gap: 0.3125rem;
        margin-right: 0.75rem;
    }
    .window-lights i {
        width: 0.4375rem;
        height: 0.4375rem;
        border-radius: 50%;
        background: #e58d8d;
    }
    .window-lights i:nth-child(2) {
        background: #e7c276;
    }
    .window-lights i:last-child {
        background: #84c595;
    }
    .log-window-body {
        position: relative;
        height: calc(100% - 2.5rem);
        overflow: hidden;
        padding: 0 0.75rem;
        font-family: var(--mono);
        font-size: 0.625rem;
    }
    .log-stream {
        --log-row-step: 1.125rem;
        position: absolute;
        top: 0;
        bottom: 0.75rem;
        left: 0.75rem;
        right: 0.75rem;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        overflow: hidden;
    }
    .log-row {
        display: grid;
        flex: 0 0 var(--log-row-step);
        grid-template-columns: 7rem 2.75rem 1fr;
        gap: 0.625rem;
        line-height: 1.8;
        color: var(--muted);
    }
    .log-cell {
        min-width: 0;
        white-space: pre;
    }
    .log-level {
        color: var(--green);
    }
    .log-row.warn .log-level {
        color: var(--orange);
    }
    .log-source {
        color: var(--text);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }
    @keyframes pulse {
        0%,
        100% {
            box-shadow: 0 0 0 0.1875rem transparent;
        }
        50% {
            box-shadow: 0 0 0 0.3125rem var(--accent-soft);
        }
    }

    @media (max-width: 640px) {
        .log-window {
            display: none;
        }
        .log-stream {
            --log-row-step: 0.9rem;
        }
        .log-row {
            grid-template-columns: 5.75rem 2.5rem 1fr;
            font-size: 0.5rem;
        }
    }
</style>
