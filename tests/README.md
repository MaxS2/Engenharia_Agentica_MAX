# Verification map

The index of every proof on the **base**. Tests live with the thing
they prove, not only under `tests/`.

| Location | Proves |
|---|---|
| `tests/end-to-end/` | Live SFTP, Java, PostgreSQL, evidence — all five types |
| `tests/contracts/` | Cross-component contract oracles, one per type |
| `tests/unit/` | Loaders, workflows, worker, recovery, Make facade |
| `tests/postgres/` | Real `COPY`, procedures, rollback |
| `tests/security/` | Adversarial worker and transport probes |
| `legacy/processor/src/test/` | Java parser and privacy, per type |
| `gen/tests/` | DataGen bytes, encoding, privacy, per type |
| `validation/oracle/tests/` | Independent correctness oracles, one suite per type |

Lakehouse and modern ingestion tests are not on this tree.

## Coverage by type

| Surface | 01 | 02 | 03 | 04 | 05 |
|---|:-:|:-:|:-:|:-:|:-:|
| Contract bytes and layout — `gen/tests/contract/` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Generator encoding — `gen/tests/unit/` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Generator privacy — `gen/tests/security/` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Java conversion and privacy — `legacy/processor/src/test/` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Cross-component contract — `tests/contracts/` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Independent oracle — `validation/oracle/tests/` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Loader boundary — `tests/unit/test_typeNN_loader.py` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Typed workflow — `tests/unit/test_typeNN_workflow.py` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Transactional rollback — `tests/postgres/` | ✓ | ✓ | ✓ | ✓ | ✓ |
| Live acceptance — `tests/end-to-end/run_typeNN_suite.py` | ✓ | ✓ | ✓ | ✓ | ✓ |

Types `01` and `02` keep bespoke live suites. Types `03`–`05` use
`typed_acceptance.py`. All five run under `make test-e2e TYPE=all`.

Type `01` is the exception in names: generic PostgreSQL tables, no
`type01` migration, DataGen `type_01` vs runtime `type01`. That is a
real estate, not two implementations.

## Running them

```bash
make check                  # source, build, Java, and the fast suites
make test-postgres          # rollback-only, against a live database
make test-type01            # the full Type 01 proof on a fresh runtime
make test-e2e TYPE=all      # live acceptance, all five types
make test-worker-e2e        # the autonomous worker on a clean runtime
make test                   # check + test-postgres + worker acceptance
```

Live suites do not erase runtime state. Canonical batch IDs are
immutable. Clean or isolate before repeating a live suite:
`make clean CONFIRM=clean-runtime`, then `make deploy`.

## What must not change

- **A gate that cannot fail is worse than no gate.** Prove it red
  before accepting it green.
- **Fixture names.** They are resolved by path across four languages.
