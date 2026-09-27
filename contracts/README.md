# Contracts — the source of correctness

**Five approved types, and every file here outranks the code.**

This folder is the source of correctness for the **base**. DataGen, Java,
PostgreSQL, and the independent oracles read from here. **None of them may
define correctness themselves.** When two implementations disagree, this
folder decides which one is wrong — and when an implementation and a contract
disagree, the implementation is the bug.

Modern and the detector will read the same files later. They do not have to
exist for a batch to be correct.

```text
contracts/
├── common/      2,821   the transport envelopes shared by every type
└── types/       2,927   the five file types, their fixtures, their oracles
    ├── registry.yaml            the index — a type not listed does not exist
    ├── 01-card-settlement/
    ├── 02-instant-payment-events/
    ├── 03-payment-slip-settlement/
    ├── 04-ted-transfer-settlement/
    └── 05-merchant-fee-assessment/
```

Two doors, and they answer different questions:

| Folder | Question | Guide |
|---|---|---|
| [`common/`](common/README.md) | *How do components hand a batch to each other?* | Manifests, receipts, checksum grammar |
| [`types/`](types/README.md) | *What is a correct batch of type NN?* | Layout, CSV, privacy, reconciliation, fixtures |

---

## Why the split is load-bearing

`common/` is the boundary **between independently implemented components**. Its
three JSON Schemas are closed and type-dispatched: a consumer validates the
envelope, then branches on the exact `file_type.number`. **The file extension
never selects a parser** — `.dat` is used by Types `01` and `04`, and `.csv` by
Type `05` and by every sanitized output in the system.

`types/` is the boundary **between an implementation and the truth**. Each type
folder answers exactly four questions in four files — how to read the bytes,
what to emit, what must never leave, and how to know it added up — plus a
`main/` folder holding the approved inputs and outputs that *are* the oracle.

Keeping them apart is what lets a reviewer check money rules without reading
transport rules, and privacy rules without reading either.

---

## Schema is necessary and not sufficient

JSON Schema closes artifact shape and filename grammar. It cannot express
equality across artifacts — that the same batch ID and date appear in the
envelope, the filename, *and* the raw header. Those links are **mandatory
semantic validation**, performed before publication, intake, conversion, or
loading, and they are enumerated in [`common/README.md`](common/README.md).

Examples that no schema can state: Type `02` requires
`returned_count <= row_count`; Type `03` requires adjacent source-record
numbers and exact physical-count/byte-size agreement.

An implementation that validates the schema and skips the semantic checks will
pass its own tests and still accept a cross-paired batch.

---

## Five types, and why exactly these five

Every entry in [`registry.yaml`](types/registry.yaml) is
`approved-for-implementation`. They were chosen to stress five different
things — a parser that handles one tells you nothing about the next:

| Type | Layout | Exercises |
|---|---|---|
| `01` | Fixed-width, COBOL overpunch | Signed values encoded in the last byte |
| `02` | Delimited with escaping | Escaped separators inside content |
| `03` | Paired 240-byte segments | Cross-record grammar; a logical row spans two physical ones |
| `04` | Heterogeneous record widths | One file, several record shapes |
| `05` | Semicolon CSV, decimal commas | Locale encoding and HALF_UP rounding |

Types `06` and later are **not on the base**. They are not in
`registry.yaml`. A new type arrives as a docked kit (specification +
expected outputs) and is refused until that oracle exists. Empty type
folders are not allowed.

## Base sign-off

This folder is signed off as the five-type base:

- `common/` envelopes the SFTP handoff (source manifest, sanitized manifest, DataGen receipt, checksum sidecar).
- `types/registry.yaml` lists exactly five types, all `approved-for-implementation`.
- Each type has the four YAMLs, a README, and a `main/` oracle (accept, boundary, type-specific edge, malformed refusal, source-defect refusal).
- Every type’s processing route is the base path: raw SFTP → Java privacy → sanitized SFTP → PostgreSQL procedures → reconciliation.
- Tolerances are zero. Rejection codes are append-only and declared on each `layout.yaml`.

A sixth type is a later kit, not a change to this sign-off.

---

## What must never change

- **An expected value, fixture, or oracle** — never edited to turn a red gate
  green. Green must come from the referee.
- **A fixture filename** — resolved by path across four languages.
- **A tolerance** — they are zero everywhere and stay zero. A tolerance is how
  an unexplained cent becomes an accepted cent.
- **A `canonical_rejection_code` already in use** — the vocabulary is
  append-only.

Contract changes are versioned through `contract_version` and `layout_version`,
never by editing an approved artifact in place.

> **No oracle, no build.** A specification that does not ship its expected
> outputs cannot be adjudicated, so the factory refuses it before doing any
> work.

Adding a type? The checklist is in [`types/README.md`](types/README.md).
