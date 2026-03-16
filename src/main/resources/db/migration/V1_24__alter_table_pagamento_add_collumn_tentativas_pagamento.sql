ALTER TABLE pagamento
ADD COLUMN tentativas_pagamento INT;

UPDATE pagamento
SET tentativas_pagamento = 0;

ALTER TABLE pagamento
ALTER COLUMN tentativas_pagamento SET NOT NULL;

